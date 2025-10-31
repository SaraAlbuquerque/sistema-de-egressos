package com.egressos.service;

import com.egressos.auth.Passwords;
import com.egressos.dao.UsuariosDao;
import com.egressos.model.Papel;
import com.egressos.model.Usuario;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UsersService {
    private final UsuariosDao dao = new UsuariosDao();

    public Usuario criarUsuario(String email, String nome, Papel papel) {
        Optional<Usuario> ja = dao.buscarPorEmail(email);
        if (ja.isPresent()) throw new IllegalArgumentException("E-mail já cadastrado.");

        String temp = Passwords.randomTempPassword();
        Usuario u = new Usuario(
                UUID.randomUUID().toString(), email, nome, papel,
                Passwords.sha256(temp), false, Instant.now()
        );
        dao.salvarOuAtualizar(u);
        u.setSenhaHash(temp);
        return u;
    }

    public List<Usuario> listar() { return dao.listar(); }
}
