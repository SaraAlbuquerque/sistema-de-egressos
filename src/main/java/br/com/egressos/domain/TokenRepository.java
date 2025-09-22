package br.com.egressos.domain;

import java.util.Optional;

public interface TokenRepository {
    Optional<TokenRecuperacaoSenha> find(String token);
    void save(TokenRecuperacaoSenha token);
    void invalidate(String token);
}
