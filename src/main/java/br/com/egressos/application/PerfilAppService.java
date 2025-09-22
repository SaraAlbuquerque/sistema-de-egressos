package br.com.egressos.application;

import br.com.egressos.application.dto.*;
import br.com.egressos.domain.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PerfilAppService {
    private final UsuarioRepository usuarios;

    public PerfilAppService(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    public void completarCadastroEgresso(String email, EgressoPerfilDTO dto) {
        Usuario u = usuarios.findByEmail(email).orElseThrow();
        if (!(u instanceof Egresso eg)) throw new IllegalStateException("Apenas egresso");
        eg.setNascimento(LocalDate.parse(dto.nascimento()));
        eg.setAnoConclusao(dto.anoConclusao());
        try { eg.setGithubURL(new URL(dto.githubURL())); } catch (MalformedURLException e) { throw new IllegalArgumentException("URL GitHub inválida"); }
        eg.setRedes(parseRedes(dto.redes()));
        usuarios.save(eg);
    }

    public void atualizarDadosBasicos(String email, String novoEmail, List<RedeSocialDTO> redes) {
        Usuario u = usuarios.findByEmail(email).orElseThrow();
        if (!email.equals(novoEmail) && usuarios.findByEmail(novoEmail).isPresent())
            throw new IllegalArgumentException("E-mail já em uso");
        u.setEmail(novoEmail);
        if (u instanceof Egresso eg) {
            eg.setRedes(parseRedes(redes));
        }
        usuarios.save(u);
    }

    private List<RedeSocial> parseRedes(List<RedeSocialDTO> redes) {
        if (redes==null) return new ArrayList<>();
        if (redes.size()>2) throw new IllegalArgumentException("Máximo 2 redes sociais");
        List<RedeSocial> out = new ArrayList<>();
        for (RedeSocialDTO rs : redes) {
            try {
                RedeSocial r = new RedeSocial(TipoRede.valueOf(rs.tipo().toUpperCase()), new URL(rs.url()));
                out.add(r);
            } catch (Exception e) { throw new IllegalArgumentException("Rede inválida: "+rs); }
        }
        return out;
    }
}
