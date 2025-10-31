package com.egressos.dao;

import com.egressos.model.EgressoProfile;

import java.nio.file.Paths;
import java.util.*;

public class EgressosDao {
    private static final String HEADER = "usuario_id,curso,ano_formacao,area_atuacao,escolaridade_atual,empregado,empresa_atual,cidade,estado,pais";
    private final CsvStore store = new CsvStore(Paths.get("data/egressos.csv"), HEADER);

    public Optional<EgressoProfile> porUsuarioId(String usuarioId) {
        return store.readAll().stream()
                .filter(r -> r[0].equals(usuarioId))
                .findFirst()
                .map(this::toProfile);
    }

    public java.util.List<EgressoProfile> listarTodos() {
        return store.readAll().stream().map(this::toProfile).collect(java.util.stream.Collectors.toList());
    }

    public void salvarOuAtualizar(EgressoProfile p) {
        java.util.List<String[]> rows = store.readAll();
        boolean updated = false;
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i)[0].equals(p.getUsuarioId())) {
                rows.set(i, toRow(p));
                updated = true;
                break;
            }
        }
        if (!updated) rows.add(toRow(p));
        store.overwrite(rows, HEADER);
    }

    private EgressoProfile toProfile(String[] r) {
        EgressoProfile p = new EgressoProfile();
        p.setUsuarioId(r[0]);
        p.setCurso(r[1]);
        p.setAnoFormacao(r[2].isBlank() ? 0 : Integer.parseInt(r[2]));
        p.setAreaAtuacao(r[3]);
        p.setEscolaridadeAtual(r[4]);
        p.setEmpregado("1".equals(r[5]));
        p.setEmpresaAtual(r[6]);
        p.setCidade(r[7]); p.setEstado(r[8]); p.setPais(r[9]);
        return p;
    }

    private String[] toRow(EgressoProfile p) {
        return new String[]{
                p.getUsuarioId(), p.getCurso(), String.valueOf(p.getAnoFormacao()),
                p.getAreaAtuacao(), p.getEscolaridadeAtual(),
                p.isEmpregado() ? "1" : "0",
                p.getEmpresaAtual(), p.getCidade(), p.getEstado(), p.getPais()
        };
    }
}
