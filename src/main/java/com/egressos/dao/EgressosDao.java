package com.egressos.dao;

import com.egressos.model.EgressoProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EgressosDao {
    private final CsvStore csv = CsvStore.get();

    private static String n(String s){ return s==null? "" : s; }

    public List<EgressoProfile> listarTodos(){
        List<EgressoProfile> out = new ArrayList<>();
        for (String[] row : csv.readAll("egressos.csv")) {
            if (row.length < 10) continue; // ao menos até 'pais'

            EgressoProfile p = new EgressoProfile();
            p.setUsuarioId(row[0]);
            p.setCurso(row[1]);
            try { p.setAnoFormacao(Integer.parseInt(row[2])); } catch (Exception e){ p.setAnoFormacao(0); }
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
            out.add(p);
        }
        return out;
    }

    public void salvarTodos(List<EgressoProfile> all){
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
                    n(p.getCidade()), n(p.getEstado()), n(p.getPais()),
                    String.valueOf(p.isPermitirExibirContato()),
                    String.valueOf(p.isPermitirExibirEmpresa())
            });
        }
        csv.writeAll("egressos.csv", out, new String[]{
                "usuarioId","curso","ano","area","escolaridade","empregado","empresa","cidade","estado","pais",
                "permitirExibirContato","permitirExibirEmpresa"
        });
    }

    public Optional<EgressoProfile> porUsuarioId(String usuarioId){
        if (usuarioId == null) return Optional.empty();
        return listarTodos().stream()
                .filter(p -> usuarioId.equals(p.getUsuarioId()))
                .findFirst();
    }

    public void salvarOuAtualizar(EgressoProfile p){
        if (p == null || p.getUsuarioId()==null || p.getUsuarioId().isBlank())
            throw new IllegalArgumentException("EgressoProfile sem usuarioId");

        List<EgressoProfile> all = listarTodos();
        boolean updated = false;
        for (int i=0; i<all.size(); i++){
            if (p.getUsuarioId().equals(all.get(i).getUsuarioId())){
                all.set(i, p);
                updated = true;
                break;
            }
        }
        if (!updated) all.add(p);
        salvarTodos(all);
    }
}
