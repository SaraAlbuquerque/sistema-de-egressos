package com.egressos.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GeradorSenhaTemporariaTest {

    private final GeradorSenhaTemporaria gerador = new GeradorSenhaTemporaria();

    @Test
    @DisplayName("Senha temporária tem 10 caracteres alfanuméricos")
    void senhaTemporariaFormatoCorreto() {
        String senha = gerador.gerarSenha();

        assertNotNull(senha);
        assertEquals(10, senha.length());
        assertTrue(senha.matches("[A-Za-z0-9]{10}"));
    }

    @Test
    @DisplayName("Geração é aleatória entre chamadas")
    void senhasGeradasSaoDiferentes() {
        Set<String> geradas = new HashSet<>();

        for (int i = 0; i < 5; i++) {
            geradas.add(gerador.gerarSenha());
        }

        assertTrue(geradas.size() > 1, "Esperado ao menos duas senhas distintas");
    }
}
