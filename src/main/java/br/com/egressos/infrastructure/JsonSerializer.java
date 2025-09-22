package br.com.egressos.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonSerializer implements Serializer {
    private final ObjectMapper mapper;
    public JsonSerializer() {
        var ptv = BasicPolymorphicTypeValidator.builder().allowIfBaseType(Object.class).build();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);
    }
    @Override public byte[] serialize(Object obj) {
        try { return mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(obj); }
        catch(Exception e){ throw new RuntimeException(e); }
    }
    @Override public <T> T deserialize(byte[] data, Class<T> type) {
        try { return mapper.readValue(data, type); }
        catch(Exception e){ throw new RuntimeException(e); }
    }
}
