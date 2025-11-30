package com.egressos.integration;

import com.egressos.auth.AuthService;
import com.egressos.auth.Passwords;
import com.egressos.dao.EgressosDao;
import com.egressos.dao.UsuariosDao;
import com.egressos.model.EgressoProfile;
import com.egressos.model.Usuario;
import com.egressos.service.EgressosService;
import com.egressos.service.EventosService;
import com.egressos.service.PasswordResetService;
import com.egressos.service.ReportsService;
import com.egressos.service.UsersService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;


import static org.junit.jupiter.api.Assertions.*;

class SystemIntegrationTest {

    private final Path dataDir = Path.of("data");
    private Path backupDir;

    @BeforeEach
    void setUp(@TempDir Path temp) throws IOException {
        backupDir = temp.resolve("backup-data");
        Files.createDirectories(backupDir);
        copyDirectory(dataDir, backupDir);

        cleanDirectory(dataDir);
    }

    @AfterEach
    void tearDown() throws IOException {
        cleanDirectory(dataDir);
        copyDirectory(backupDir, dataDir);
    }

    @Test
    void userLifecycleAndEventsArePersistedAcrossServices() throws Exception {
        UsersService users = new UsersService();
        UsersService.ResultadoCriacao created = users.criarUsuario(
                "Ana Teste", "ana@example.com", com.egressos.model.Papel.EGRESSO);

        assertFalse(created.isEmailJaExiste());
        assertNotNull(created.getUsuario());
        assertFalse(created.getSenhaTemporaria().isBlank());

        UsuariosDao usuariosDao = new UsuariosDao();
        Usuario persistedUser = usuariosDao.buscarPorEmail("ana@example.com").orElseThrow();
        persistedUser.setSenhaHash(Passwords.sha256(created.getSenhaTemporaria()));
        usuariosDao.salvarOuAtualizar(persistedUser);

        AuthService auth = new AuthService();
        Optional<Usuario> logged = auth.login("ana@example.com", created.getSenhaTemporaria());
        assertTrue(logged.isPresent());

        EgressosService egressosService = new EgressosService();
        EgressoProfile profile = new EgressoProfile();
        profile.setUsuarioId(logged.get().getId());
        profile.setCurso("Computação");
        profile.setAnoFormacao(2020);
        profile.setAreaAtuacao("TI");
        profile.setCidade("São Paulo");
        profile.setEmpresaAtual("Tech");
        profile.setAnoConclusao(2021);
        profile.setPermitirExibirContato(true);
        egressosService.salvar(profile);

        EventosService eventos = new EventosService();
        LocalDate eventDate = LocalDate.of(2023, 9, 10);
        eventos.criar(profile.getUsuarioId(), "PROMOCAO", "Promoção", "subida",
                "Empresa", eventDate, "SP", "nota");

        assertEquals(1, eventos.listarPorEgresso(profile.getUsuarioId()).size());

        ReportsService reports = new ReportsService();
        Path reportFile = Files.createTempFile("report", ".csv");
        reports.exportEventosPorTipoCsv(reportFile);

        List<String> lines = Files.readAllLines(reportFile);
        assertTrue(lines.get(0).startsWith("tipo,quantidade"));
        long exportedCount = lines.stream()
                .skip(1)
                .filter(l -> !l.isBlank())
                .map(l -> l.split(","))
                .mapToLong(arr -> Long.parseLong(arr[1]))
                .sum();
        assertEquals(1, exportedCount);

        var persistedProfile = new EgressosDao().porUsuarioId(profile.getUsuarioId());
        assertTrue(persistedProfile.isPresent());
        assertEquals("Computação", persistedProfile.get().getCurso());
    }

    @Test
    void passwordResetFlowUpdatesStoredCredentials() throws Exception {
        UsersService users = new UsersService();
        UsersService.ResultadoCriacao created = users.criarUsuario(
                "Bruno", "bruno@example.com", com.egressos.model.Papel.EGRESSO);
        String originalTemp = created.getSenhaTemporaria();

        PasswordResetService resetService = new PasswordResetService();
        Optional<String> tokenOpt = resetService.iniciar("bruno@example.com");
        assertTrue(tokenOpt.isPresent());

        String token = tokenOpt.get();

        Path resetFile = dataDir.resolve("password_resets.csv");
        List<String> beforeLines = Files.readAllLines(resetFile);
        assertTrue(beforeLines.stream().anyMatch(l -> l.contains(token) && l.endsWith(",0")));

        String newPassword = "NovaSenha@Forte123";
        assertTrue(resetService.concluir(token, newPassword));

        List<String> afterLines = Files.readAllLines(resetFile);
        assertTrue(afterLines.stream().anyMatch(l -> l.contains(token) && l.endsWith(",1")));

        AuthService auth = new AuthService();
        assertTrue(auth.login("bruno@example.com", newPassword).isPresent());
        assertTrue(auth.login("bruno@example.com", originalTemp).isEmpty());

        UsuariosDao dao = new UsuariosDao();
        Usuario persisted = dao.buscarPorEmail("bruno@example.com").orElseThrow();
        assertFalse(persisted.isPrecisaTrocarSenha());
        assertEquals(Passwords.sha256(newPassword), persisted.getSenhaHash());
    }

    private void cleanDirectory(Path dir) {
        if (!Files.exists(dir)) return;

        try (java.util.stream.Stream<Path> s = Files.walk(dir)) {
            s.sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        if (p.equals(dir)) return;
                        try {
                            Files.deleteIfExists(p);
                        } catch (java.nio.file.AccessDeniedException ex) {
                            System.err.println("Aviso: sem acesso para apagar: " + p + " (" + ex.getMessage() + ")");
                        } catch (IOException ex) {
                            throw new RuntimeException("Falha limpando diretório de teste: " + p, ex);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("Falha ao percorrer diretório de teste: " + dir, e);
        }
    }

    private static void copyDirectory(Path source, Path target) throws IOException {
        if (!Files.exists(source)) return;
        Files.walk(source).forEach(path -> {
            try {
                Path relative = source.relativize(path);
                Path dest = target.resolve(relative);
                if (Files.isDirectory(path)) {
                    Files.createDirectories(dest);
                } else {
                    Files.createDirectories(dest.getParent());
                    Files.copy(path, dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
