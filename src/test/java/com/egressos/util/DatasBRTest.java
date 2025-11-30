package com.egressos.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

class DatasBRTest {

    @Test
    @DisplayName("Formatação retorna vazio para valores nulos")
    void formatacoesNulas() {
        assertEquals("", DatasBR.format(null));
        assertEquals("", DatasBR.formatInstant(null));
    }

    @Test
    @DisplayName("LocalDate é formatado e revertido corretamente")
    void formatarEParsearData() {
        LocalDate data = LocalDate.of(2023, 5, 10);
        String formatada = DatasBR.format(data);

        assertEquals("10/05/2023", formatada);
        assertEquals(data, DatasBR.parse(formatada));
        assertNull(DatasBR.parse("   "));
    }

    @Test
    @DisplayName("Instant utiliza o fuso horário padrão para conversão")
    void formatarInstantComFusoPadrao() {
        TimeZone zonaOriginal = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        try {
            Instant instante = Instant.parse("2023-01-15T12:00:00Z");
            assertEquals("15/01/2023", DatasBR.formatInstant(instante));
        } finally {
            TimeZone.setDefault(zonaOriginal);
        }
    }
}
