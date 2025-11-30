package com.egressos.service;

import com.egressos.auth.Passwords;
import com.egressos.dao.CsvStore;
import com.egressos.dao.PasswordResetsDao;
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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PasswordResetServiceTest {

    private static final Path USUARIOS_PATH = Paths.get("data/usuarios.csv");
    private static final String USUARIOS_HEADER = "id,email,nome,papel,senha_hash,precisa_trocar_senha,criado_em";

    private static final Path PASSWORD_RESETS_PATH = Paths.get("data/password_resets.csv");
    private static final String PASSWORD_RESETS_HEADER = "token,usuario_id,email,expira_em,consumido";

    private final UsuariosDao usuariosDao = new UsuariosDao();
    private final PasswordResetsDao resetsDao = new PasswordResetsDao();
    private final PasswordResetService service = new PasswordResetService();

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(USUARIOS_PATH.getParent());
        Files.writeString(USUARIOS_PATH, USUARIOS_HEADER + System.lineSeparator());
        Files.writeString(PASSWORD_RESETS_PATH, PASSWORD_RESETS_HEADER + System.lineSeparator());
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(USUARIOS_PATH);
        Files.deleteIfExists(PASSWORD_RESETS_PATH);
    }

    @Test
    void iniciarRetornaEmptyQuandoEmailNaoExiste() {
        Optional<String> token = service.iniciar("desconhecido@example.com");

        assertTrue(token.isEmpty());
        assertEquals(0, resetsDao.buscarAtivo("qualquer").stream().count());
    }

    @Test
    void iniciarParaEmailValidoGeraTokenERegistro() {
        Usuario user = criarUsuario("10", "alvo@example.com", "SenhaValida#1");
        usuariosDao.salvarOuAtualizar(user);

        Optional<String> token = service.iniciar("alvo@example.com");

        assertTrue(token.isPresent());
        Optional<String[]> registro = resetsDao.buscarAtivo(token.get());
        assertTrue(registro.isPresent());
        assertEquals("alvo@example.com", registro.get()[2]);
        assertEquals("0", registro.get()[4]);
    }

    @Test
    void concluirRetornaFalseParaTokenInexistenteOuExpirado() throws IOException {
        Usuario user = criarUsuario("11", "expira@example.com", "SenhaValida#1");
        usuariosDao.salvarOuAtualizar(user);

        assertFalse(service.concluir("token-invalido", "SenhaValida#2"));

        List<String[]> rows = new java.util.ArrayList<>();
        rows.add(new String[]{
                "token-expirado", user.getId(), user.getEmail(),
                Instant.now().minus(1, ChronoUnit.HOURS).toString(), "0"
        });
        new CsvStore(PASSWORD_RESETS_PATH, PASSWORD_RESETS_HEADER)
                .overwrite(rows, PASSWORD_RESETS_HEADER);

        assertFalse(service.concluir("token-expirado", "SenhaValida#2"));
    }

    @Test
    void concluirLancaExcecaoQuandoSenhaFraca() {
        Usuario user = criarUsuario("12", "fraco@example.com", "SenhaValida#1");
        usuariosDao.salvarOuAtualizar(user);
        String token = service.iniciar("fraco@example.com").orElseThrow();

        assertThrows(IllegalArgumentException.class, () ->
                service.concluir(token, "curta")
        );

        assertEquals("0", resetsDao.buscarAtivo(token).orElseThrow()[4]);
    }

    @Test
    void concluirComSucessoAtualizaHashConsomeTokenELimpaTrocaObrigatoria() {
        Usuario user = criarUsuario("13", "ok@example.com", "SenhaValida#1");
        user.setPrecisaTrocarSenha(true);
        usuariosDao.salvarOuAtualizar(user);
        String token = service.iniciar("ok@example.com").orElseThrow();

        boolean resultado = service.concluir(token, "NovaSenha#123");

        assertTrue(resultado);
        Usuario atualizado = usuariosDao.buscarPorEmail("ok@example.com").orElseThrow();
        assertEquals(Passwords.sha256("NovaSenha#123"), atualizado.getSenhaHash());
        assertFalse(atualizado.isPrecisaTrocarSenha());

        Optional<String[]> registro = resetsDao.buscarAtivo(token);
        assertTrue(registro.isEmpty());

        List<String[]> linhas = new CsvStore(PASSWORD_RESETS_PATH, PASSWORD_RESETS_HEADER).readAll();
        assertEquals("1", linhas.get(0)[4]);
    }

    private Usuario criarUsuario(String id, String email, String senha) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setEmail(email);
        u.setNome("Nome " + id);
        u.setPapel(Papel.EGRESSO);
        u.setSenhaHash(Passwords.sha256(senha));
        u.setPrecisaTrocarSenha(false);
        u.setCriadoEm(Instant.now());
        return u;
    }
}
