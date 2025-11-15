package com.egressos.controller;

import com.egressos.dao.UsuariosDao;
import com.egressos.model.Papel;
import com.egressos.model.Usuario;
import com.egressos.service.GeradorSenhaTemporaria;
import com.egressos.service.PasswordUtil;
import com.egressos.service.ValidadorSenha;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class UsuarioController {

    private final UsuariosDao usuariosDao;
    private final ValidadorSenha validadorSenha;
    private final GeradorSenhaTemporaria geradorSenhaTemporaria;

    public UsuarioController() {
        this.usuariosDao = new UsuariosDao();
        this.validadorSenha = new ValidadorSenha();
        this.geradorSenhaTemporaria = new GeradorSenhaTemporaria();
    }

    public Usuario autenticar(String email, String senhaDigitada) {
        Optional<Usuario> opt = usuariosDao.buscarPorEmail(email);
        if (opt.isEmpty()) return null;
        Usuario u = opt.get();
        boolean ok = validadorSenha.validarSenhaLogin(senhaDigitada, u.getSenhaHash());
        if (!ok) return null;
        return u;
    }

    public boolean redefinirSenha(String email, String novaSenha) {
        if (!validadorSenha.validarForcaSenha(novaSenha)) return false;
        Optional<Usuario> opt = usuariosDao.buscarPorEmail(email);
        if (opt.isEmpty()) return false;
        Usuario u = opt.get();
        String hash = PasswordUtil.hash(novaSenha);
        u.setSenhaHash(hash);
        u.setPrecisaTrocarSenha(false);
        usuariosDao.salvarOuAtualizar(u);
        return true;
    }

    public enum ResultadoAlterarSenha {
        SENHA_ATUAL_INCORRETA,
        NOVA_SENHA_FRACA,
        OK
    }

    public ResultadoAlterarSenha alterarSenha(String email, String senhaAtual, String novaSenha) {
        Optional<Usuario> opt = usuariosDao.buscarPorEmail(email);
        if (opt.isEmpty()) return ResultadoAlterarSenha.SENHA_ATUAL_INCORRETA;
        Usuario u = opt.get();
        boolean atualOk = validadorSenha.validarSenhaLogin(senhaAtual, u.getSenhaHash());
        if (!atualOk) return ResultadoAlterarSenha.SENHA_ATUAL_INCORRETA;
        if (!validadorSenha.validarForcaSenha(novaSenha)) return ResultadoAlterarSenha.NOVA_SENHA_FRACA;
        String novoHash = PasswordUtil.hash(novaSenha);
        u.setSenhaHash(novoHash);
        u.setPrecisaTrocarSenha(false);
        usuariosDao.salvarOuAtualizar(u);
        return ResultadoAlterarSenha.OK;
    }

    public static class ResultadoCadastroUsuario {
        private final boolean emailJaExiste;
        private final Usuario usuarioCriado;
        private final String senhaTemporaria;

        public ResultadoCadastroUsuario(boolean emailJaExiste, Usuario usuarioCriado, String senhaTemporaria) {
            this.emailJaExiste = emailJaExiste;
            this.usuarioCriado = usuarioCriado;
            this.senhaTemporaria = senhaTemporaria;
        }

        public boolean isEmailJaExiste() {
            return emailJaExiste;
        }

        public Usuario getUsuarioCriado() {
            return usuarioCriado;
        }

        public String getSenhaTemporaria() {
            return senhaTemporaria;
        }
    }

    public ResultadoCadastroUsuario cadastrarUsuario(String nome, String email, Papel papel) {
        Optional<Usuario> ja = usuariosDao.buscarPorEmail(email);
        if (ja.isPresent()) {
            return new ResultadoCadastroUsuario(true, null, null);
        }
        String senhaTemporaria = geradorSenhaTemporaria.gerarSenha();
        String hash = PasswordUtil.hash(senhaTemporaria);
        Usuario u = new Usuario(
                UUID.randomUUID().toString(),
                email,
                nome,
                papel,
                hash,
                true,
                Instant.now()
        );
        usuariosDao.salvarOuAtualizar(u);
        return new ResultadoCadastroUsuario(false, u, senhaTemporaria);
    }
}
