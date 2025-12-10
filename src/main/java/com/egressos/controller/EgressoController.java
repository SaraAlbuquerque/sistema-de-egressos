package com.egressos.controller;

import com.egressos.dao.EgressosDao;
import com.egressos.dao.UsuariosDao;
import com.egressos.model.EgressoProfile;
import com.egressos.model.Usuario;

import java.time.LocalDate;
import java.util.Optional;

public class EgressoController {

    private final EgressosDao egressosDao;
    private final UsuariosDao usuariosDao;

    public EgressoController() {
        this.egressosDao = new EgressosDao();
        this.usuariosDao = new UsuariosDao();
    }

    public boolean completarCadastro(String usuarioId,
                                     LocalDate dataNascimento,
                                     Integer anoFormacao,
                                     String nickGithub,
                                     String redeSocial1,
                                     String redeSocial2) {
        if (dataNascimento == null || anoFormacao == null || nickGithub == null || nickGithub.isBlank()) {
            return false;
        }
        Optional<EgressoProfile> opt = egressosDao.porUsuarioId(usuarioId);
        EgressoProfile perfil = opt.orElseGet(EgressoProfile::new);
        perfil.setUsuarioId(usuarioId);
        perfil.setDataNascimento(dataNascimento);
        perfil.setAnoFormacao(anoFormacao);
        perfil.setNickGithub(nickGithub);
        perfil.setRedeSocial1(redeSocial1);
        perfil.setRedeSocial2(redeSocial2);
        egressosDao.salvarOuAtualizar(perfil);
        return true;
    }

    public boolean atualizarContato(String usuarioId,
                                    String novoEmail,
                                    String redeSocial1,
                                    String redeSocial2) {
        Optional<Usuario> optUser = usuariosDao.buscarPorId(usuarioId);
        if (optUser.isEmpty()) return false;
        Usuario u = optUser.get();
        if (novoEmail != null && !novoEmail.isBlank()) {
            u.setEmail(novoEmail);
        }
        usuariosDao.salvarOuAtualizar(u);
        Optional<EgressoProfile> optPerfil = egressosDao.porUsuarioId(usuarioId);
        EgressoProfile perfil = optPerfil.orElseGet(EgressoProfile::new);
        perfil.setUsuarioId(usuarioId);
        perfil.setRedeSocial1(redeSocial1);
        perfil.setRedeSocial2(redeSocial2);
        egressosDao.salvarOuAtualizar(perfil);
        return true;
    }
}
