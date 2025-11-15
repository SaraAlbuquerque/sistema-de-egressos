package com.egressos.controller;

import com.egressos.model.EventoChave;
import com.egressos.service.EventosService;

import java.time.LocalDate;
import java.util.List;

public class EventoController {

    private final EventosService eventosService;

    public EventoController() {
        this.eventosService = new EventosService();
    }

    public EventoChave adicionarEvento(String egressoId,
                                       String tipo,
                                       String titulo,
                                       String descricao,
                                       String organizacao,
                                       LocalDate data,
                                       String local,
                                       String observacoes) {
        return eventosService.criar(egressoId, tipo, titulo, descricao, organizacao, data, local, observacoes);
    }

    public void excluirEvento(String eventoId) {
        eventosService.excluir(eventoId);
    }

    public List<EventoChave> listarPorEgresso(String egressoId) {
        return eventosService.listarPorEgresso(egressoId);
    }
}
