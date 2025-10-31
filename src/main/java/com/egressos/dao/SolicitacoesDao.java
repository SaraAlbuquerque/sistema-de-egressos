package com.egressos.dao;

import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class SolicitacoesDao {
    private static final String HEADER = "id,nome,email,papel,criado_em";
    private final CsvStore store = new CsvStore(Paths.get("data/solicitacoes.csv"), HEADER);

    public void criar(String id, String nome, String email, String papel, Instant criadoEm) {
        List<String[]> rows = store.readAll();
        rows.add(new String[]{id, nome, email, papel, criadoEm.toString()});
        store.overwrite(rows, HEADER);
    }

    public List<String[]> listar() { return store.readAll(); }

    public void remover(String id) {
        List<String[]> rows = store.readAll().stream()
                .filter(r -> !r[0].equals(id)).collect(Collectors.toList());
        store.overwrite(rows, HEADER);
    }
}
