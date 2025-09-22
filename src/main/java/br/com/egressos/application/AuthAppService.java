package br.com.egressos.application;

import br.com.egressos.application.dto.*;
import br.com.egressos.domain.*;

import java.time.Duration;
import java.util.Optional;

public class AuthAppService {
    private final UsuarioRepository usuarios;
    private final PasswordHasher hasher;
    private final PasswordPolicyService policy;
    private final ResetTokenService tokenService;
    private final TokenRepository tokens;

    public AuthAppService(UsuarioRepository usuarios, PasswordHasher hasher,
                          PasswordPolicyService policy, ResetTokenService tokenService,
                          TokenRepository tokens) {
        this.usuarios = usuarios;
        this.hasher = hasher;
        this.policy = policy;
        this.tokenService = tokenService;
        this.tokens = tokens;
    }

    public LoginResult autenticar(LoginDTO dto) {
        Optional<Usuario> u = usuarios.findByEmail(dto.email());
        if (u.isEmpty() || !u.get().isAtivo()) return new LoginResult(false,false,false,null);
        if (!hasher.verify(dto.senha(), u.get().getSenhaHash())) return new LoginResult(false,false,false,null);
        boolean precisaCompletar = (u.get() instanceof Egresso eg) &&
            (eg.getNascimento()==null || eg.getAnoConclusao()==null || eg.getGithubURL()==null);
        return new LoginResult(true, u.get().isSenhaTemporaria(), precisaCompletar, u.get().getTipo());
    }

    public void trocarSenhaNoLogin(String email, String senhaAtual, String novaSenha) {
        Usuario u = usuarios.findByEmail(email).orElseThrow();
        if (!hasher.verify(senhaAtual, u.getSenhaHash())) throw new IllegalArgumentException("Senha atual incorreta");
        policy.validar(novaSenha);
        u.setSenhaHash(hasher.hash(novaSenha));
        u.setSenhaTemporaria(false);
        usuarios.save(u);
    }

    public void trocarSenhaEmSessao(String email, String senhaAtual, String novaSenha) {
        trocarSenhaNoLogin(email, senhaAtual, novaSenha);
    }

    public String solicitarReset(ResetSolicitacaoDTO dto) {
        Optional<Usuario> u = usuarios.findByEmail(dto.email());
        if (u.isEmpty()) return "OK"; // não revelar existência
        TokenRecuperacaoSenha t = tokenService.gerar(dto.email(), Duration.ofHours(1));
        tokens.save(t);
        return t.getToken(); // para testes
    }

    public void redefinirSenha(ResetSenhaDTO dto) {
        TokenRecuperacaoSenha t = tokens.find(dto.token()).orElseThrow(() -> new IllegalArgumentException("token inválido"));
        if (t.isUsado()) throw new IllegalStateException("token já usado");
        if (t.expiradoEm(java.time.LocalDateTime.now())) throw new IllegalStateException("token expirado");
        Usuario u = usuarios.findByEmail(t.getEmailUsuario()).orElseThrow();
        policy.validar(dto.novaSenha());
        u.setSenhaHash(hasher.hash(dto.novaSenha()));
        u.setSenhaTemporaria(false);
        usuarios.save(u);
        t.setUsado(true);
        tokens.save(t);
    }
}
