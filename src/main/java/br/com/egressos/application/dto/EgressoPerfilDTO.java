package br.com.egressos.application.dto;
import java.util.List;
public record EgressoPerfilDTO(String nascimento, Integer anoConclusao, String githubURL, List<RedeSocialDTO> redes) {}
