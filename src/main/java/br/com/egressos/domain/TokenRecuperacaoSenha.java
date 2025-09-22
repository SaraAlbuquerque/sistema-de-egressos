package br.com.egressos.domain;

import java.time.LocalDateTime;

public class TokenRecuperacaoSenha {
    private String token;
    private String emailUsuario;
    private LocalDateTime criadoEm;
    private LocalDateTime expiraEm;
    private boolean usado;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getEmailUsuario() { return emailUsuario; }
    public void setEmailUsuario(String emailUsuario) { this.emailUsuario = emailUsuario; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
    public LocalDateTime getExpiraEm() { return expiraEm; }
    public void setExpiraEm(LocalDateTime expiraEm) { this.expiraEm = expiraEm; }
    public boolean isUsado() { return usado; }
    public void setUsado(boolean usado) { this.usado = usado; }

    public boolean expiradoEm(LocalDateTime agora) { return expiraEm!=null && agora.isAfter(expiraEm); }
}
