package com.egressos.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvStoreTest {

    @TempDir
    Path tempDir;

    @Test
    void readAllRetornaListaVaziaQuandoArquivoNaoExiste() {
        Path file = tempDir.resolve("inexistente.csv");
        CsvStore store = new CsvStore(file, "col1,col2");
        assertDoesNotThrow(() -> Files.deleteIfExists(file));

        List<String[]> linhas = store.readAll();

        assertTrue(linhas.isEmpty());
    }

    @Test
    void criaArquivoComCabecalhoEIgnoraLinhasEmBranco() throws IOException {
        Path file = tempDir.resolve("dados.csv");
        String header = "a,b,c";
        CsvStore store = new CsvStore(file, header);

        Files.writeString(file, header + System.lineSeparator() +
                "1,2,3" + System.lineSeparator() + System.lineSeparator());

        List<String[]> linhas = store.readAll();

        assertEquals(1, linhas.size());
        assertArrayEquals(new String[]{"1", "2", "3"}, linhas.get(0));
    }

    @Test
    void overwriteMantemCabecalhoPersonalizado() {
        Path file = tempDir.resolve("dados2.csv");
        String header = "x,y";
        CsvStore store = new CsvStore(file, header);

        List<String[]> linhas = new java.util.ArrayList<>();
        linhas.add(new String[]{"a", "b"});

        store.overwrite(linhas, header);

        List<String> todasLinhas = assertDoesNotThrow(() -> Files.readAllLines(file));
        assertEquals(header, todasLinhas.get(0));
        assertEquals("a,b", todasLinhas.get(1));
    }

    @Test
    void metodosDeInstanciaNoSingletonLancaExcecao() {
        CsvStore singleton = CsvStore.get();

        assertThrows(IllegalStateException.class, singleton::readAll);
        assertThrows(IllegalStateException.class, () -> singleton.overwrite(List.of(), "h1,h2"));
    }
}
