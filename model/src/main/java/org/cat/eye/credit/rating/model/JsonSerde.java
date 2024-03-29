package org.cat.eye.credit.rating.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;
import java.util.Map;

public class JsonSerde<T extends JSONSerdeCompatible> implements Serializer<T>, Deserializer<T>, Serde<T> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(String topic, byte[] data) {

        if (data == null) {
            return null;
        }

        try {
            return (T) OBJECT_MAPPER.readValue(data, JSONSerdeCompatible.class);
        } catch (final IOException e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public Serializer<T> serializer() {
        return this;
    }

    @Override
    public Deserializer<T> deserializer() {
        return this;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, T data) {

        if (data == null) {
            return null;
        }

        try {
            return OBJECT_MAPPER.writeValueAsBytes(data);
        } catch (final Exception e) {
            throw new SerializationException("Error serializing JSON message", e);
        }
    }

    @Override
    public void close() {

    }

}
