package br.com.egressos.domain;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Egresso extends Usuario {
    private LocalDate nascimento;
    private Integer anoConclusao;
    private URL githubURL;
    private List<RedeSocial> redes = new ArrayList<>();

    public LocalDate getNascimento() { return nascimento; }
    public void setNascimento(LocalDate nascimento) { this.nascimento = nascimento; }
    public Integer getAnoConclusao() { return anoConclusao; }
    public void setAnoConclusao(Integer anoConclusao) { this.anoConclusao = anoConclusao; }
    public URL getGithubURL() { return githubURL; }
    public void setGithubURL(URL githubURL) { this.githubURL = githubURL; }
    public List<RedeSocial> getRedes() { return redes; }
    public void setRedes(List<RedeSocial> redes) { this.redes = redes; }
}
