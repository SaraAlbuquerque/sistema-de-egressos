package com.egressos.dao;

import com.egressos.model.EventoChave;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class EventosDao {
    private static final String HEADER = "id,egresso_id,tipo,titulo,descricao,organizacao,data_iso,local,observacoes";
    private final CsvStore store = new CsvStore(Paths.get("data/eventos.csv"), HEADER);

    public List<EventoChave> porEgresso(String egressoId) {
        return store.readAll().stream()
                .filter(r -> r[1].equals(egressoId))
                .map(this::toEvento)
                .sorted(Comparator.comparing(EventoChave::getData))
                .collect(Collectors.toList());
    }

    public List<EventoChave> listarTodos() {
        return store.readAll().stream()
                .map(this::toEvento)
                .sorted(Comparator.comparing(EventoChave::getEgressoId).thenComparing(EventoChave::getData))
                .collect(Collectors.toList());
    }

    public void salvar(EventoChave e) {
        List<String[]> rows = store.readAll();
        boolean updated = false;
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i)[0].equals(e.getId())) {
                rows.set(i, toRow(e)); updated = true; break;
            }
        }
        if (!updated) rows.add(toRow(e));
        store.overwrite(rows, HEADER);
    }

    public void remover(String id) {
        List<String[]> rows = store.readAll().stream()
                .filter(r -> !r[0].equals(id)).collect(Collectors.toList());
        store.overwrite(rows, HEADER);
    }

    private EventoChave toEvento(String[] r) {
        EventoChave e = new EventoChave();
        e.setId(r[0]);
        e.setEgressoId(r[1]);
        e.setTipo(r[2]);
        e.setTitulo(r[3]);
        e.setDescricao(r[4]);
        e.setOrganizacao(r[5]);
        e.setData(r[6].isBlank() ? LocalDate.of(1970,1,1) : LocalDate.parse(r[6]));
        e.setLocal(r[7]);
        e.setObservacoes(r[8]);
        return e;
    }

    private String[] toRow(EventoChave e) {
        return new String[]{
                e.getId(), e.getEgressoId(), e.getTipo(), e.getTitulo(),
                e.getDescricao(), e.getOrganizacao(),
                e.getData()==null? "" : e.getData().toString(),
                e.getLocal(), e.getObservacoes()
        };
    }
}
