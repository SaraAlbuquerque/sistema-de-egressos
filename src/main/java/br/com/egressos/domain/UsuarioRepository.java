package br.com.egressos.domain;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> list(String filtros);
    void save(Usuario usuario);
    void delete(String email);
}
