package com.egressos.service;

import com.egressos.auth.Passwords;
import com.egressos.dao.UsuariosDao;
import com.egressos.model.Papel;
import com.egressos.model.Usuario;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class UsersService {

    private final UsuariosDao dao = new UsuariosDao();

    public static class ResultadoCriacao {
        private final boolean emailJaExiste;
        private final Usuario usuario;
        private final String senhaTemporaria;

        public ResultadoCriacao(boolean emailJaExiste, Usuario usuario, String senhaTemporaria) {
            this.emailJaExiste = emailJaExiste;
            this.usuario = usuario;
            this.senhaTemporaria = senhaTemporaria;
        }

        public boolean isEmailJaExiste() {
            return emailJaExiste;
        }

        public Usuario getUsuario() {
            return usuario;
        }

        public String getSenhaTemporaria() {
            return senhaTemporaria;
        }
    }

    public ResultadoCriacao criarUsuario(String nome, String email, Papel papel) {
        Optional<Usuario> existente = dao.buscarPorEmail(email);
        if (existente.isPresent()) {
            return new ResultadoCriacao(true, null, null);
        }
        String temp = Passwords.randomTempPassword();
        String hash = PasswordUtil.hash(temp);
        Usuario u = new Usuario(
                UUID.randomUUID().toString(),
                email,
                nome,
                papel,
                hash,
                true,
                Instant.now()
        );
        dao.salvarOuAtualizar(u);
        return new ResultadoCriacao(false, u, temp);
    }
}
