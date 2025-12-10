package com.egressos.dao;

import com.egressos.model.EgressoProfile;
import com.egressos.model.FiltroPesquisaEgressos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EgressosDao {
    private final CsvStore csv = CsvStore.get();

    private static String n(String s) {
        return s == null ? "" : s;
    }

    public List<EgressoProfile> listarTodos() {
        List<EgressoProfile> out = new ArrayList<>();
        for (String[] row : csv.readAll("egressos.csv")) {
            if (row.length < 10) continue;
            EgressoProfile p = new EgressoProfile();
            p.setUsuarioId(row[0]);
            p.setCurso(row[1]);
            try {
                p.setAnoFormacao(Integer.parseInt(row[2]));
            } catch (Exception e) {
                p.setAnoFormacao(0);
            }
            p.setAreaAtuacao(row[3]);
            p.setEscolaridadeAtual(row[4]);
            p.setEmpregado(Boolean.parseBoolean(row[5]));
            p.setEmpresaAtual(row[6]);
            p.setCidade(row[7]);
            p.setEstado(row[8]);
            p.setPais(row[9]);
            if (row.length > 11) {
                p.setPermitirExibirContato(Boolean.parseBoolean(row[10]));
                p.setPermitirExibirEmpresa(Boolean.parseBoolean(row[11]));
            } else {
                p.setPermitirExibirContato(false);
                p.setPermitirExibirEmpresa(false);
            }
            if (row.length > 12 && row[12] != null && !row[12].isBlank()) {
                try {
                    p.setDataNascimento(LocalDate.parse(row[12]));
                } catch (Exception e) {
                    p.setDataNascimento(null);
                }
            }
            if (row.length > 13 && row[13] != null && !row[13].isBlank()) {
                try {
                    p.setAnoFormacao(Integer.parseInt(row[13]));
                } catch (Exception e) {
                    p.setAnoFormacao(-1);
                }
            }
            if (row.length > 14) p.setNickGithub(row[14]);
            if (row.length > 15) p.setRedeSocial1(row[15]);
            if (row.length > 16) p.setRedeSocial2(row[16]);
            out.add(p);
        }
        return out;
    }

    public void salvarTodos(List<EgressoProfile> all) {
        List<String[]> out = new ArrayList<>();
        for (EgressoProfile p : all) {
            out.add(new String[]{
                    n(p.getUsuarioId()),
                    n(p.getCurso()),
                    String.valueOf(p.getAnoFormacao()),
                    n(p.getAreaAtuacao()),
                    n(p.getEscolaridadeAtual()),
                    String.valueOf(p.isEmpregado()),
                    n(p.getEmpresaAtual()),
                    n(p.getCidade()),
                    n(p.getEstado()),
                    n(p.getPais()),
                    String.valueOf(p.isPermitirExibirContato()),
                    String.valueOf(p.isPermitirExibirEmpresa()),
                    p.getDataNascimento() == null ? "" : p.getDataNascimento().toString(),
                    p.getAnoFormacao() == null ? "" : String.valueOf(p.getAnoFormacao()),
                    n(p.getNickGithub()),
                    n(p.getRedeSocial1()),
                    n(p.getRedeSocial2())
            });
        }
        csv.writeAll("egressos.csv", out, new String[]{
                "usuarioId",
                "curso",
                "anoFormacao",
                "areaAtuacao",
                "escolaridadeAtual",
                "empregado",
                "empresaAtual",
                "cidade",
                "estado",
                "pais",
                "permitirExibirContato",
                "permitirExibirEmpresa",
                "dataNascimento",
                "anoFormacao",
                "nickGithub",
                "redeSocial1",
                "redeSocial2"
        });
    }

    public Optional<EgressoProfile> porUsuarioId(String usuarioId) {
        if (usuarioId == null) return Optional.empty();
        return listarTodos().stream()
                .filter(p -> usuarioId.equals(p.getUsuarioId()))
                .findFirst();
    }

    public void salvarOuAtualizar(EgressoProfile p) {
        if (p == null || p.getUsuarioId() == null || p.getUsuarioId().isBlank())
            throw new IllegalArgumentException("EgressoProfile sem usuarioId");
        List<EgressoProfile> all = listarTodos();
        boolean updated = false;
        for (int i = 0; i < all.size(); i++) {
            if (p.getUsuarioId().equals(all.get(i).getUsuarioId())) {
                all.set(i, p);
                updated = true;
                break;
            }
        }
        if (!updated) all.add(p);
        salvarTodos(all);
    }

    public List<EgressoProfile> buscarPorFiltro(FiltroPesquisaEgressos f) {
        return listarTodos().stream().filter(p -> {
            if (f.getCurso() != null && !f.getCurso().isBlank()) {
                String curso = p.getCurso() == null ? "" : p.getCurso().toLowerCase();
                if (!curso.contains(f.getCurso().toLowerCase())) return false;
            }
            if (f.getAnoFormacao() != null) {
                if (p.getAnoFormacao() == null || !p.getAnoFormacao().equals(f.getAnoFormacao()))
                    return false;
            }
            if (f.getCidade() != null && !f.getCidade().isBlank()) {
                String cid = p.getCidade() == null ? "" : p.getCidade().toLowerCase();
                if (!cid.contains(f.getCidade().toLowerCase())) return false;
            }
            if (f.getEmpresaAtual() != null && !f.getEmpresaAtual().isBlank()) {
                String emp = p.getEmpresaAtual() == null ? "" : p.getEmpresaAtual().toLowerCase();
                if (!emp.contains(f.getEmpresaAtual().toLowerCase())) return false;
            }
            if (f.getAreaAtuacao() != null && !f.getAreaAtuacao().isBlank()) {
                String area = p.getAreaAtuacao() == null ? "" : p.getAreaAtuacao().toLowerCase();
                if (!area.contains(f.getAreaAtuacao().toLowerCase())) return false;
            }
            return true;
        }).collect(Collectors.toList());
    }
}
