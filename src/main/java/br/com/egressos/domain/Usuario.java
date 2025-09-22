package br.com.egressos.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Usuario {
    protected String email;
    protected String nome;
    protected String senhaHash;
    protected boolean senhaTemporaria;
    protected boolean ativo = true;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
    public boolean isSenhaTemporaria() { return senhaTemporaria; }
    public void setSenhaTemporaria(boolean b) { this.senhaTemporaria = b; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    @JsonIgnore
    public String getTipo(){ return getClass().getSimpleName().toUpperCase(); }
}
