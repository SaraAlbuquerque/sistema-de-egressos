package com.egressos.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class DatasBR {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private DatasBR() { }

    public static DateTimeFormatter formatter() {
        return FORMATTER;
    }

    public static String format(LocalDate date) {
        if (date == null) return "";
        return date.format(FORMATTER);
    }

    public static String formatInstant(Instant instant) {
        if (instant == null) return "";
        return instant.atZone(ZoneId.systemDefault()).toLocalDate().format(FORMATTER);
    }

    public static LocalDate parse(String text) {
        if (text == null) return null;
        String s = text.trim();
        if (s.isEmpty()) return null;
        return LocalDate.parse(s, FORMATTER);
    }
}
