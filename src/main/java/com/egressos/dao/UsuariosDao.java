package com.egressos.dao;

import com.egressos.model.Papel;
import com.egressos.model.Usuario;

import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class UsuariosDao {
    private static final String HEADER = "id,email,nome,papel,senha_hash,precisa_trocar_senha,criado_em";
    private final CsvStore store = new CsvStore(Paths.get("data/usuarios.csv"), HEADER);

    public List<Usuario> listar() {
        return store.readAll().stream().map(this::toUsuario).collect(Collectors.toList());
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return listar().stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).findFirst();
    }

    public void salvarOuAtualizar(Usuario u) {
        List<Usuario> all = listar();
        boolean updated = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(u.getId())) {
                all.set(i, u);
                updated = true;
                break;
            }
        }
        if (!updated) {
            all.add(u);
        }
        store.overwrite(all.stream().map(this::toRow).collect(Collectors.toList()), HEADER);
    }

    private Usuario toUsuario(String[] r) {
        return new Usuario(
                r[0], r[1], r[2], Papel.valueOf(r[3]), r[4],
                "1".equals(r[5]), Instant.parse(r[6])
        );
    }
    private String[] toRow(Usuario u) {
        return new String[]{
                u.getId(), u.getEmail(), u.getNome(), u.getPapel().name(),
                u.getSenhaHash(),
                u.isPrecisaTrocarSenha() ? "1" : "0",
                u.getCriadoEm().toString()
        };
    }
}
