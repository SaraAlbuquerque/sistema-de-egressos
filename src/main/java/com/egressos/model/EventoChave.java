package com.egressos.model;

import java.time.LocalDate;
import java.util.UUID;

public class EventoChave {
    private String id;
    private String egressoId;
    private String tipo;
    private String titulo;
    private String descricao;
    private String organizacao;
    private LocalDate data;
    private String local;
    private String observacoes;

    public static String newId() { return UUID.randomUUID().toString(); }

    public String getId() { return id; }
    public String getEgressoId() { return egressoId; }
    public String getTipo() { return tipo; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public String getOrganizacao() { return organizacao; }
    public LocalDate getData() { return data; }
    public String getLocal() { return local; }
    public String getObservacoes() { return observacoes; }

    public void setId(String id) { this.id = id; }
    public void setEgressoId(String egressoId) { this.egressoId = egressoId; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setOrganizacao(String organizacao) { this.organizacao = organizacao; }
    public void setData(LocalDate data) { this.data = data; }
    public void setLocal(String local) { this.local = local; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
