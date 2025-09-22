package br.com.egressos.domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class ResetTokenService {
    public TokenRecuperacaoSenha gerar(String email, Duration ttl) {
        TokenRecuperacaoSenha t = new TokenRecuperacaoSenha();
        t.setToken(UUID.randomUUID().toString());
        t.setEmailUsuario(email);
        t.setCriadoEm(LocalDateTime.now());
        t.setExpiraEm(LocalDateTime.now().plus(ttl));
        t.setUsado(false);
        return t;
    }
}
