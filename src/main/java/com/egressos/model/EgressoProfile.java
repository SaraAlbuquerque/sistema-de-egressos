package com.egressos.model;

import java.util.Objects;

public class EgressoProfile {
    private String usuarioId;
    private String curso;
    private int anoFormacao;
    private String areaAtuacao;
    private String escolaridadeAtual;
    private boolean empregado;
    private String empresaAtual;
    private String cidade;
    private String estado;
    private String pais;

    public EgressoProfile() {}

    public EgressoProfile(String usuarioId, String curso, int anoFormacao, String areaAtuacao, String escolaridadeAtual, boolean empregado, String empresaAtual, String cidade, String estado, String pais) {
        this.usuarioId = usuarioId;
        this.curso = curso;
        this.anoFormacao = anoFormacao;
        this.areaAtuacao = areaAtuacao;
        this.escolaridadeAtual = escolaridadeAtual;
        this.empregado = empregado;
        this.empresaAtual = empresaAtual;
        this.cidade = cidade;
        this.estado = estado;
        this.pais = pais;
    }

    public String getUsuarioId() { return usuarioId; }
    public String getCurso() { return curso; }
    public int getAnoFormacao() { return anoFormacao; }
    public String getAreaAtuacao() { return areaAtuacao; }
    public String getEscolaridadeAtual() { return escolaridadeAtual; }
    public boolean isEmpregado() { return empregado; }
    public String getEmpresaAtual() { return empresaAtual; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
    public String getPais() { return pais; }

    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public void setCurso(String curso) { this.curso = curso; }
    public void setAnoFormacao(int anoFormacao) { this.anoFormacao = anoFormacao; }
    public void setAreaAtuacao(String areaAtuacao) { this.areaAtuacao = areaAtuacao; }
    public void setEscolaridadeAtual(String escolaridadeAtual) { this.escolaridadeAtual = escolaridadeAtual; }
    public void setEmpregado(boolean empregado) { this.empregado = empregado; }
    public void setEmpresaAtual(String empresaAtual) { this.empresaAtual = empresaAtual; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setPais(String pais) { this.pais = pais; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EgressoProfile)) return false;
        EgressoProfile that = (EgressoProfile) o;
        return Objects.equals(usuarioId, that.usuarioId);
    }
    @Override public int hashCode() { return Objects.hash(usuarioId); }

    @Override public String toString() {
        return "EgressoProfile{" +
                "usuarioId='" + usuarioId + '\'' +
                ", curso='" + curso + '\'' +
                ", anoFormacao=" + anoFormacao +
                ", areaAtuacao='" + areaAtuacao + '\'' +
                ", escolaridadeAtual='" + escolaridadeAtual + '\'' +
                ", empregado=" + empregado +
                ", empresaAtual='" + empresaAtual + '\'' +
                ", cidade='" + cidade + '\'' +
                ", estado='" + estado + '\'' +
                ", pais='" + pais + '\'' +
                '}';
    }
}
