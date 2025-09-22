package br.com.egressos.application;

import br.com.egressos.application.dto.*;
import br.com.egressos.domain.*;

import java.util.List;

public class AdminUsuariosAppService {
    private final UsuarioRepository usuarios;
    private final PasswordHasher hasher;

    public AdminUsuariosAppService(UsuarioRepository usuarios, PasswordHasher hasher) {
        this.usuarios = usuarios;
        this.hasher = hasher;
    }

    public String criar(CriarUsuarioDTO dto) {
        if (usuarios.findByEmail(dto.email()).isPresent())
            throw new IllegalArgumentException("E-mail já cadastrado");
        Usuario u = switch (dto.tipo().toUpperCase()) {
            case "COORDENADOR" -> new Coordenador();
            case "DOCENTE" -> new Docente();
            case "EGRESSO" -> new Egresso();
            default -> throw new IllegalArgumentException("Tipo inválido");
        };
        u.setEmail(dto.email());
        u.setNome(dto.nome());
        String temp = PasswordGenerator.generate();
        u.setSenhaHash(hasher.hash(temp));
        u.setSenhaTemporaria(true);
        usuarios.save(u);
        return temp; // coordenador repassa por canal externo
    }

    public void atualizar(UsuarioDTO dto) {
        Usuario u = usuarios.findByEmail(dto.email()).orElseThrow();
        u.setNome(dto.nome());
        u.setAtivo(dto.ativo());
        usuarios.save(u);
    }

    public void inativar(String email){ Usuario u = usuarios.findByEmail(email).orElseThrow(); u.setAtivo(false); usuarios.save(u); }
    public void excluir(String email){ usuarios.delete(email); }
    public List<UsuarioDTO> listar(String filtros){ return usuarios.list(filtros).stream()
        .map(u -> new UsuarioDTO(u.getEmail(), u.getNome(), u.getTipo(), u.isAtivo())).toList(); }
}
