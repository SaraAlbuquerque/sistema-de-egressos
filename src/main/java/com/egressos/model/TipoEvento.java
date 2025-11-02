package com.egressos.model;

public enum TipoEvento {
    PROMOCAO,
    PROJETO,
    PREMIO,
    PUBLICACAO,
    OUTRO;

    public static TipoEvento fromString(String s) {
        if (s == null || s.isBlank()) return OUTRO;
        String k = s.trim().toUpperCase();
        for (TipoEvento t : values()) {
            if (t.name().equals(k)) return t;
        }
        return OUTRO; // tolera valores antigos
    }
}
