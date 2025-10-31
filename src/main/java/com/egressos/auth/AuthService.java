package com.egressos.auth;

import com.egressos.dao.UsuariosDao;
import com.egressos.model.Usuario;

import java.util.Optional;

public class AuthService {
    private final UsuariosDao usuariosDao = new UsuariosDao();
    private Usuario usuarioLogado;

    public Optional<Usuario> login(String email, String senha) {
        return usuariosDao.buscarPorEmail(email).filter(u ->
                u.getSenhaHash().equals(Passwords.sha256(senha)));
    }

    public void setUsuarioLogado(Usuario u) { this.usuarioLogado = u; }
    public Usuario getUsuarioLogado() { return usuarioLogado; }

    public void trocarSenhaObrigatoria(Usuario u, String novaSenha) {
        u.setSenhaHash(Passwords.sha256(novaSenha));
        u.setPrecisaTrocarSenha(false);
        usuariosDao.salvarOuAtualizar(u);
    }
}
