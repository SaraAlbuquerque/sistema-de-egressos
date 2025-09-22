package br.com.egressos.infrastructure; public interface Serializer { byte[] serialize(Object obj); <T> T deserialize(byte[] data, Class<T> type);}
