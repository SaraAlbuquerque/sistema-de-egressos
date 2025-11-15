package com.egressos.auth;

import com.egressos.dao.UsuariosDao;
import com.egressos.model.Usuario;

import java.util.Optional;

public class AuthService {

    private final UsuariosDao usuariosDao = new UsuariosDao();
    private Usuario usuarioLogado;

    public enum ResultadoTrocaObrigatoria {
        SENHA_ANTIGA_INCORRETA,
        NOVA_SENHA_FRACA,
        OK
    }

    public Optional<Usuario> login(String email, String senha) {
        return usuariosDao.buscarPorEmail(email).filter(u ->
                u.getSenhaHash().equals(Passwords.sha256(senha)));
    }

    public void setUsuarioLogado(Usuario u) { this.usuarioLogado = u; }
    public Usuario getUsuarioLogado() { return usuarioLogado; }

    public ResultadoTrocaObrigatoria trocarSenhaObrigatoria(Usuario u, String senhaAtual, String novaSenha) {

        String hashAtual = Passwords.sha256(senhaAtual);
        if (!hashAtual.equals(u.getSenhaHash())) {
            return ResultadoTrocaObrigatoria.SENHA_ANTIGA_INCORRETA;
        }

        if (!PasswordPolicy.isValid(novaSenha)) {
            return ResultadoTrocaObrigatoria.NOVA_SENHA_FRACA;
        }

        String novoHash = Passwords.sha256(novaSenha);
        u.setSenhaHash(novoHash);
        u.setPrecisaTrocarSenha(false);
        usuariosDao.salvarOuAtualizar(u);

        return ResultadoTrocaObrigatoria.OK;
    }
}
