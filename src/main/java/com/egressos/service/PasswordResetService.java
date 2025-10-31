package com.egressos.service;

import com.egressos.auth.PasswordPolicy;
import com.egressos.auth.Passwords;
import com.egressos.dao.PasswordResetsDao;
import com.egressos.dao.UsuariosDao;
import com.egressos.model.Usuario;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

public class PasswordResetService {
    private final UsuariosDao usuarios = new UsuariosDao();
    private final PasswordResetsDao resets = new PasswordResetsDao();

    public Optional<String> iniciar(String email) {
        Optional<Usuario> u = usuarios.buscarPorEmail(email);
        if (u.isEmpty()) return Optional.empty();
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(
                (UUID.randomUUID().toString() + System.nanoTime()).getBytes()
        );
        resets.criar(token, u.get().getId(), email, Instant.now().plus(15, ChronoUnit.MINUTES));
        return Optional.of(token);
    }

    public boolean concluir(String token, String novaSenha) {
        var reg = resets.buscarAtivo(token);
        if (reg.isEmpty()) return false;

        String msg = PasswordPolicy.violationMessage(novaSenha);
        if (msg != null) {
            throw new IllegalArgumentException(msg);
        }

        String userId = reg.get()[1];
        Usuario user = usuarios.listar().stream()
                .filter(x -> x.getId().equals(userId))
                .findFirst()
                .orElseThrow();

        user.setSenhaHash(Passwords.sha256(novaSenha));
        user.setPrecisaTrocarSenha(false);
        usuarios.salvarOuAtualizar(user);
        resets.consumir(token);
        return true;
    }
}
