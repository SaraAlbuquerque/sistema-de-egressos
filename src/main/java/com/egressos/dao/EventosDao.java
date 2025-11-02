package com.egressos.dao;

import com.egressos.model.EventoChave;
import com.egressos.model.TipoEvento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CSV: eventos.csv
 * Header: id,egressoId,tipo,titulo,organizacao,local,descricao,observacoes,data
 */
public class EventosDao {
    private final CsvStore csv = CsvStore.get();

    private static String n(String s){ return s==null? "" : s; }

    public List<EventoChave> listarTodos(){
        List<EventoChave> out = new ArrayList<>();
        for (String[] row : csv.readAll("eventos.csv")) {
            // tolerância: se arquivo antigo não tiver todas colunas, preenche o que der
            EventoChave e = new EventoChave();
            if (row.length > 0) e.setId(row[0]);
            if (row.length > 1) e.setEgressoId(row[1]);
            if (row.length > 2) e.setTipo(TipoEvento.fromString(row[2]));
            if (row.length > 3) e.setTitulo(row[3]);
            if (row.length > 4) e.setOrganizacao(row[4]);
            if (row.length > 5) e.setLocal(row[5]);
            if (row.length > 6) e.setDescricao(row[6]);
            if (row.length > 7) e.setObservacoes(row[7]);
            if (row.length > 8 && row[8]!=null && !row[8].isBlank()) {
                e.setData(LocalDate.parse(row[8]));
            }
            out.add(e);
        }
        return out;
    }

    public void salvarTodos(List<EventoChave> all){
        List<String[]> out = new ArrayList<>();
        for (EventoChave e : all) {
            out.add(new String[]{
                    n(e.getId()),
                    n(e.getEgressoId()),
                    e.getTipo()==null? "" : e.getTipo().name(),
                    n(e.getTitulo()),
                    n(e.getOrganizacao()),
                    n(e.getLocal()),
                    n(e.getDescricao()),
                    n(e.getObservacoes()),
                    e.getData()==null? "" : e.getData().toString()
            });
        }
        csv.writeAll("eventos.csv", out, new String[]{
                "id","egressoId","tipo","titulo","organizacao","local","descricao","observacoes","data"
        });
    }

    // ====== Métodos exigidos pelo seu EventosService ======

    public List<EventoChave> porEgresso(String egressoId){
        return listarTodos().stream()
                .filter(e -> egressoId != null && egressoId.equals(e.getEgressoId()))
                .collect(Collectors.toList());
    }

    /** upsert por ID (se existir substitui; se não existir, adiciona) */
    public void salvar(EventoChave e){
        List<EventoChave> all = listarTodos();
        boolean updated = false;
        for (int i=0; i<all.size(); i++){
            if (all.get(i).getId()!=null && all.get(i).getId().equals(e.getId())){
                all.set(i, e);
                updated = true;
                break;
            }
        }
        if (!updated) {
            // se não vier id, gera um
            if (e.getId()==null || e.getId().isBlank()){
                e.setId(EventoChave.newId());
            }
            all.add(e);
        }
        salvarTodos(all);
    }

    public void remover(String eventoId){
        if (eventoId == null || eventoId.isBlank()) return;
        List<EventoChave> all = listarTodos();
        all.removeIf(ev -> eventoId.equals(ev.getId()));
        salvarTodos(all);
    }
}
