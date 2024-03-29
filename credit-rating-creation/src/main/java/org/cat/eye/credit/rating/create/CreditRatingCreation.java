package org.cat.eye.credit.rating.create;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.cat.eye.credit.rating.model.JsonSerde;
import org.cat.eye.credit.rating.model.application.request.ReserveApplicationNumberRequest;
import org.cat.eye.credit.rating.model.application.response.ReserveApplicationNumberResponse;
import org.cat.eye.credit.rating.model.dictionary.IrsRateByCustomerSegmentCode;
import org.cat.eye.credit.rating.model.omni.request.CreditProfileCreateRequest;
import org.cat.eye.credit.rating.model.omni.response.CreditProfileCreateResponse;
import org.cat.eye.credit.rating.model.omni.response.CreditProfileData;
import org.cat.eye.credit.rating.model.omni.response.State;
import org.cat.eye.credit.rating.model.omni.response.Status;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CreditRatingCreation {

    private final KStream<UUID, CreditProfileCreateRequest> creditProfileCreateStream;

    private final KStream<UUID, ReserveApplicationNumberResponse> appNumberStream;

    private final GlobalKTable<Integer, IrsRateByCustomerSegmentCode> segmentCodeDictionary;

    @PostConstruct
    public void init() {

         KStream<UUID, CreditProfileCreateRequest> creditProfileCreateStream_Phase_1 = creditProfileCreateStream
                .peek((key, value) ->
                        System.out.println("Принят запрос с ID [" + key + "] от клиента " + value.participant().surname()))
                .map((key, value) -> {
                    System.out.println("Трансформация запроса c ID [" + key + "] от клиента " + value.participant().surname());
                    return new KeyValue<>(key, value);
                });

        creditProfileCreateStream_Phase_1
                .mapValues(v -> new ReserveApplicationNumberRequest())
                .to("app-number-request");

        creditProfileCreateStream_Phase_1
                .leftJoin(
                        appNumberStream,
                        valueJoiner,
                        JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofSeconds(3)),
                        StreamJoined.with(new Serdes.UUIDSerde(), new JsonSerde<>(), new JsonSerde<>())
                )
                .peek((key, value) ->
                        System.out.println("Обработка запроса с ID [" + key + "] от клиента " + value.data().state().name()))
                .leftJoin(
                        segmentCodeDictionary,
                        joinMapper,
                        tableValueJoiner
                )
                .to("credit-rating-response");
    }

    private final ValueJoiner<CreditProfileCreateRequest, ReserveApplicationNumberResponse, CreditProfileCreateResponse>
            valueJoiner = (left, right) -> {
                State state = new State("code", left.participant().name(), true,null);
                CreditProfileData profileData = new CreditProfileData(
                        right != null ? right.reservedAppNumber() : null, 1, null,"rawID", state
                );
                return new CreditProfileCreateResponse(Status.SUCCESS, new Date().getTime(), profileData);
            };

    private final KeyValueMapper<UUID, CreditProfileCreateResponse, Integer> joinMapper = (k, v) -> v.data().segmentCodeId();

    private final ValueJoiner<CreditProfileCreateResponse, IrsRateByCustomerSegmentCode, CreditProfileCreateResponse>
            tableValueJoiner = (left, right) -> {
                State state = new State("code", left.data().state().name(), true,null);
                CreditProfileData profileData = new CreditProfileData(
                        left.data().appNumber(), 1, right != null ? right.rate() : null,"rawID", state
                );
                return new CreditProfileCreateResponse(Status.SUCCESS, new Date().getTime(), profileData);
            };

}
