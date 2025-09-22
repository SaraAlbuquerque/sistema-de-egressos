package br.com.egressos;

import br.com.egressos.application.*;
import br.com.egressos.application.dto.*;
import br.com.egressos.domain.*;
import br.com.egressos.infrastructure.*;

import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        var storage = new FileStorage();
        var ser = new JsonSerializer();
        var basePath = Path.of("data");
        var userRepo = new UsuarioFileRepository(storage, ser, basePath);
        var tokenRepo = new TokenFileRepository(storage, ser, basePath);

        var hasher = new PasswordHasher();
        var policy = new PasswordPolicyService();
        var tokenSvc = new ResetTokenService();

        var admin = new AdminUsuariosAppService(userRepo, hasher);
        var auth = new AuthAppService(userRepo, hasher, policy, tokenSvc, tokenRepo);
        var perfil = new PerfilAppService(userRepo);

        // Demonstração: criar egresso
        String temp = admin.criar(new CriarUsuarioDTO("egresso4@exemplo.com","Maria Egresso","EGRESSO"));
        System.out.println("Senha temporária: " + temp);

        // Login com senha temporária
        System.out.println(auth.autenticar(new LoginDTO("egresso4@exemplo.com", temp)));

        // Trocar senha no login
        auth.trocarSenhaNoLogin("egresso4@exemplo.com", temp, "NovaSenha1");
        System.out.println("Senha trocada.");

        // Completar cadastro
        var perfilDto = new EgressoPerfilDTO("1995-05-10", 2019, "https://github.com/maria",
            List.of(new RedeSocialDTO("LINKEDIN","https://linkedin.com/in/maria")));
        perfil.completarCadastroEgresso("egresso4@exemplo.com", perfilDto);
        System.out.println("Perfil completo.");

        // Reset de senha
        String token = auth.solicitarReset(new ResetSolicitacaoDTO("egresso4@exemplo.com"));
        System.out.println("Token (teste): " + token);
        auth.redefinirSenha(new ResetSenhaDTO(token, "OutraSenha1"));
        System.out.println("Senha redefinida via token.");

        // Listar
        System.out.println(admin.listar("tipo=EGRESSO;ativo=true"));
    }
}
