package com.egressos.service;

import com.egressos.dao.EgressosDao;
import com.egressos.model.EgressoProfile;

import java.util.Optional;

public class EgressosService {
    private final EgressosDao dao = new EgressosDao();

    public Optional<EgressoProfile> obter(String usuarioId) { return dao.porUsuarioId(usuarioId); }
    public void salvar(EgressoProfile p) { dao.salvarOuAtualizar(p); }
}
