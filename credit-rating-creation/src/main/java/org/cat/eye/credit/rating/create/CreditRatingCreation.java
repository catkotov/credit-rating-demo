package org.cat.eye.credit.rating.create;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.cat.eye.credit.rating.model.JsonSerde;
import org.cat.eye.credit.rating.model.application.response.ReserveApplicationNumberResponse;
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

    private final KStream<UUID, CreditProfileCreateRequest> kStream;

    private final KStream<UUID, ReserveApplicationNumberResponse> appNumberStream;

    @PostConstruct
    public void init() {

        kStream.to("app-number-request");

        kStream.foreach((key, value) ->
                System.out.println("Принят запрос с ID [" + key + "] от клиента " + value.participant().surname())
        );

        KStream<UUID, CreditProfileCreateResponse> resultStream = kStream.join(
                appNumberStream,
                valueJoiner,
                JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofMinutes(1)),
                StreamJoined.with(new Serdes.UUIDSerde(), new JsonSerde<>(), new JsonSerde<>())
        );

        resultStream.to("credit-rating-response");
    }

    private final ValueJoiner<CreditProfileCreateRequest, ReserveApplicationNumberResponse, CreditProfileCreateResponse>
            valueJoiner = (left, right) -> {
                State state = new State("code", left.participant().name(), true,null);
                CreditProfileData profileData = new CreditProfileData(right.reservedAppNumber(), "rawID", state);
                return new CreditProfileCreateResponse(Status.SUCCESS, new Date().getTime(), profileData);
            };

}
