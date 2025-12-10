package com.egressos.model;

import java.time.LocalDate;

public class EgressoProfile {
    private String usuarioId;
    private String nome;
    private String curso;
    private Integer anoFormacao;
    private String areaAtuacao;
    private String escolaridadeAtual;
    private boolean empregado;
    private String empresaAtual;
    private String cidade;
    private String estado;
    private String pais;
    private boolean permitirExibirContato;
    private boolean permitirExibirEmpresa;
    private LocalDate dataNascimento;
    private String nickGithub;
    private String redeSocial1;
    private String redeSocial2;

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public Integer getAnoFormacao() {
        return anoFormacao;
    }

    public void setAnoFormacao(Integer anoFormacao) {
        this.anoFormacao = anoFormacao;
    }

    public String getAreaAtuacao() {
        return areaAtuacao;
    }

    public void setAreaAtuacao(String areaAtuacao) {
        this.areaAtuacao = areaAtuacao;
    }

    public String getEscolaridadeAtual() {
        return escolaridadeAtual;
    }

    public void setEscolaridadeAtual(String escolaridadeAtual) {
        this.escolaridadeAtual = escolaridadeAtual;
    }

    public boolean isEmpregado() {
        return empregado;
    }

    public void setEmpregado(boolean empregado) {
        this.empregado = empregado;
    }

    public String getEmpresaAtual() {
        return empresaAtual;
    }

    public void setEmpresaAtual(String empresaAtual) {
        this.empresaAtual = empresaAtual;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public boolean isPermitirExibirContato() {
        return permitirExibirContato;
    }

    public void setPermitirExibirContato(boolean permitirExibirContato) {
        this.permitirExibirContato = permitirExibirContato;
    }

    public boolean isPermitirExibirEmpresa() {
        return permitirExibirEmpresa;
    }

    public void setPermitirExibirEmpresa(boolean permitirExibirEmpresa) {
        this.permitirExibirEmpresa = permitirExibirEmpresa;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }


    public String getNickGithub() {
        return nickGithub;
    }

    public void setNickGithub(String nickGithub) {
        this.nickGithub = nickGithub;
    }

    public String getRedeSocial1() {
        return redeSocial1;
    }

    public void setRedeSocial1(String redeSocial1) {
        this.redeSocial1 = redeSocial1;
    }

    public String getRedeSocial2() {
        return redeSocial2;
    }

    public void setRedeSocial2(String redeSocial2) {
        this.redeSocial2 = redeSocial2;
    }
}
