package com.egressos.model;

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

    // LGPD
    private boolean permitirExibirContato; // e-mail/contato
    private boolean permitirExibirEmpresa;

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }

    public int getAnoFormacao() { return anoFormacao; }
    public void setAnoFormacao(int anoFormacao) { this.anoFormacao = anoFormacao; }

    public String getAreaAtuacao() { return areaAtuacao; }
    public void setAreaAtuacao(String areaAtuacao) { this.areaAtuacao = areaAtuacao; }

    public String getEscolaridadeAtual() { return escolaridadeAtual; }
    public void setEscolaridadeAtual(String escolaridadeAtual) { this.escolaridadeAtual = escolaridadeAtual; }

    public boolean isEmpregado() { return empregado; }
    public void setEmpregado(boolean empregado) { this.empregado = empregado; }

    public String getEmpresaAtual() { return empresaAtual; }
    public void setEmpresaAtual(String empresaAtual) { this.empresaAtual = empresaAtual; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public boolean isPermitirExibirContato() { return permitirExibirContato; }
    public void setPermitirExibirContato(boolean permitirExibirContato) { this.permitirExibirContato = permitirExibirContato; }

    public boolean isPermitirExibirEmpresa() { return permitirExibirEmpresa; }
    public void setPermitirExibirEmpresa(boolean permitirExibirEmpresa) { this.permitirExibirEmpresa = permitirExibirEmpresa; }
}
