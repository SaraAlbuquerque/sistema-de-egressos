package com.egressos.model;

import java.time.Instant;
import java.util.Objects;

public class Usuario {
    private String id;
    private String email;
    private String nome;
    private Papel papel;
    private String senhaHash;
    private boolean precisaTrocarSenha;
    private Instant criadoEm;

    public Usuario(String id, String email, String nome, Papel papel,
                   String senhaHash, boolean precisaTrocarSenha, Instant criadoEm) {
        this.id = id;
        this.email = email;
        this.nome = nome;
        this.papel = papel;
        this.senhaHash = senhaHash;
        this.precisaTrocarSenha = precisaTrocarSenha;
        this.criadoEm = criadoEm;
    }

    public Usuario() {
    }


    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getNome() { return nome; }
    public Papel getPapel() { return papel; }
    public String getSenhaHash() { return senhaHash; }
    public boolean isPrecisaTrocarSenha() { return precisaTrocarSenha; }
    public Instant getCriadoEm() { return criadoEm; }


    public void setId(String id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setNome(String nome) { this.nome = nome; }
    public void setPapel(Papel papel) { this.papel = papel; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
    public void setPrecisaTrocarSenha(boolean precisaTrocarSenha) { this.precisaTrocarSenha = precisaTrocarSenha; }
    public void setCriadoEm(Instant criadoEm) { this.criadoEm = criadoEm; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }

    @Override public String toString() {
        return "Usuario{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", nome='" + nome + '\'' +
                ", papel=" + papel +
                ", criadoEm=" + criadoEm +
                '}';
    }
}
