package com.egressos.service;

import com.egressos.auth.AuthService;
import com.egressos.auth.Passwords;
import com.egressos.dao.UsuariosDao;
import com.egressos.model.Papel;
import com.egressos.model.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private static final Path USUARIOS_PATH = Paths.get("data/usuarios.csv");
    private static final String USUARIOS_HEADER = "id,email,nome,papel,senha_hash,precisa_trocar_senha,criado_em";

    private final UsuariosDao usuariosDao = new UsuariosDao();
    private final AuthService authService = new AuthService();

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(USUARIOS_PATH.getParent());
        Files.writeString(USUARIOS_PATH, USUARIOS_HEADER + System.lineSeparator());
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(USUARIOS_PATH);
    }

    @Test
    void loginComCredenciaisValidasRetornaUsuario() {
        Usuario usuario = novoUsuario("1", "user@example.com", "SenhaF0rte#", true);
        usuariosDao.salvarOuAtualizar(usuario);

        Optional<Usuario> resultado = authService.login("user@example.com", "SenhaF0rte#");

        assertTrue(resultado.isPresent());
        assertEquals(usuario.getEmail(), resultado.get().getEmail());
    }

    @Test
    void loginComSenhaInvalidaRetornaVazio() {
        Usuario usuario = novoUsuario("2", "user2@example.com", "OutraSenha#1", true);
        usuariosDao.salvarOuAtualizar(usuario);

        assertTrue(authService.login("user2@example.com", "senha-errada").isEmpty());
        assertTrue(authService.login("naoexiste@example.com", "senha-errada").isEmpty());
    }

    @Test
    void trocarSenhaObrigatoriaRetornaErroQuandoSenhaAtualIncorreta() {
        Usuario usuario = novoUsuario("3", "alguem@example.com", "SenhaBoa#1", true);
        usuariosDao.salvarOuAtualizar(usuario);

        AuthService.ResultadoTrocaObrigatoria resultado =
                authService.trocarSenhaObrigatoria(usuario, "errada", "NovaSenha#123");

        assertEquals(AuthService.ResultadoTrocaObrigatoria.SENHA_ANTIGA_INCORRETA, resultado);
        assertEquals(Passwords.sha256("SenhaBoa#1"), usuario.getSenhaHash());
    }

    @Test
    void trocarSenhaObrigatoriaRetornaErroQuandoNovaSenhaFraca() {
        Usuario usuario = novoUsuario("4", "pessoa@example.com", "SenhaBoa#1", true);
        usuariosDao.salvarOuAtualizar(usuario);

        AuthService.ResultadoTrocaObrigatoria resultado =
                authService.trocarSenhaObrigatoria(usuario, "SenhaBoa#1", "curta");

        assertEquals(AuthService.ResultadoTrocaObrigatoria.NOVA_SENHA_FRACA, resultado);
        assertEquals(Passwords.sha256("SenhaBoa#1"), usuario.getSenhaHash());
    }

    @Test
    void trocarSenhaObrigatoriaAtualizaHashEPersisteQuandoSucesso() {
        Usuario usuario = novoUsuario("5", "troca@example.com", "SenhaBoa#1", true);
        usuariosDao.salvarOuAtualizar(usuario);

        AuthService.ResultadoTrocaObrigatoria resultado =
                authService.trocarSenhaObrigatoria(usuario, "SenhaBoa#1", "NovaSenha#123");

        assertEquals(AuthService.ResultadoTrocaObrigatoria.OK, resultado);
        Optional<Usuario> salvo = usuariosDao.buscarPorEmail("troca@example.com");
        assertTrue(salvo.isPresent());
        assertEquals(Passwords.sha256("NovaSenha#123"), salvo.get().getSenhaHash());
        assertFalse(salvo.get().isPrecisaTrocarSenha());
    }

    private Usuario novoUsuario(String id, String email, String senha, boolean precisaTrocar) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setEmail(email);
        u.setNome("Nome " + id);
        u.setPapel(Papel.EGRESSO);
        u.setSenhaHash(Passwords.sha256(senha));
        u.setPrecisaTrocarSenha(precisaTrocar);
        u.setCriadoEm(Instant.now());
        return u;
    }
}
