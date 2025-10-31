package com.egressos.service;

import com.egressos.dao.EventosDao;
import com.egressos.model.EventoChave;

import java.time.LocalDate;
import java.util.List;

public class EventosService {
    private final EventosDao dao = new EventosDao();

    public List<EventoChave> listarPorEgresso(String egressoId) { return dao.porEgresso(egressoId); }

    public EventoChave criar(String egressoId, String tipo, String titulo, String desc,
                             String org, LocalDate data, String local, String obs) {
        EventoChave e = new EventoChave();
        e.setId(EventoChave.newId());
        e.setEgressoId(egressoId);
        e.setTipo(tipo); e.setTitulo(titulo); e.setDescricao(desc);
        e.setOrganizacao(org); e.setData(data); e.setLocal(local); e.setObservacoes(obs);
        dao.salvar(e);
        return e;
    }

    public void atualizar(EventoChave e) { dao.salvar(e); }
    public void excluir(String eventoId) { dao.remover(eventoId); }
}
