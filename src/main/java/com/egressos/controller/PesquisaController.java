package com.egressos.controller;

import com.egressos.dao.EgressosDao;
import com.egressos.dao.UsuariosDao;
import com.egressos.model.EgressoProfile;
import com.egressos.model.FiltroPesquisaEgressos;
import com.egressos.model.Usuario;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class PesquisaController {

    private final EgressosDao egressosDao;
    private final UsuariosDao usuariosDao;

    public PesquisaController() {
        this.egressosDao = new EgressosDao();
        this.usuariosDao = new UsuariosDao();
    }

    public List<EgressoProfile> pesquisarEgressos(FiltroPesquisaEgressos filtros) {
        List<EgressoProfile> base = egressosDao.buscarPorFiltro(filtros);
        Map<String, Usuario> porId = usuariosDao.listar().stream()
                .collect(Collectors.toMap(Usuario::getId, u -> u, (a, b) -> a));
        String nomeFiltro = filtros.getNome();
        if (nomeFiltro == null || nomeFiltro.isBlank()) {
            return base;
        }
        String nomeLower = nomeFiltro.toLowerCase();
        return base.stream().filter(p -> {
            Usuario u = porId.get(p.getUsuarioId());
            if (u == null) return false;
            String nome = u.getNome();
            if (nome == null) return false;
            return nome.toLowerCase().contains(nomeLower);
        }).collect(Collectors.toList());
    }
}
