package com.egressos.model;

import java.time.LocalDate;
import java.util.UUID;

public class EventoChave {
    private String id;
    private String egressoId;
    private TipoEvento tipo;
    private String titulo;
    private String organizacao;
    private String local;
    private String descricao;
    private String observacoes; // novo: compatível com seu service
    private LocalDate data;

    public static String newId() { return UUID.randomUUID().toString(); }

    public EventoChave(){}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEgressoId() { return egressoId; }
    public void setEgressoId(String egressoId) { this.egressoId = egressoId; }

    public TipoEvento getTipo() { return tipo; }
    public void setTipo(TipoEvento tipo) { this.tipo = tipo; }

    public void setTipo(String tipoTexto) { this.tipo = TipoEvento.fromString(tipoTexto); }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getOrganizacao() { return organizacao; }
    public void setOrganizacao(String organizacao) { this.organizacao = organizacao; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
}
