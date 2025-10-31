package com.egressos.dao;

import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;

public class PasswordResetsDao {
    private static final String HEADER = "token,usuario_id,email,expira_em,consumido";
    private final CsvStore store = new CsvStore(Paths.get("data/password_resets.csv"), HEADER);

    public void criar(String token, String usuarioId, String email, Instant expira) {
        List<String[]> rows = store.readAll();
        rows.add(new String[]{token, usuarioId, email, expira.toString(), "0"});
        store.overwrite(rows, HEADER);
    }

    public Optional<String[]> buscarAtivo(String token) {
        Instant now = Instant.now();
        return store.readAll().stream()
                .filter(r -> r[0].equals(token) && "0".equals(r[4]) && Instant.parse(r[3]).isAfter(now))
                .findFirst();
    }

    public void consumir(String token) {
        List<String[]> rows = store.readAll();
        for (String[] r : rows) if (r[0].equals(token)) r[4] = "1";
        store.overwrite(rows, HEADER);
    }
}
