package com.egressos.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordPolicyTest {

    @Test
    @DisplayName("Senha que atende todos os requisitos é considerada válida")
    void senhaValida() {
        String senha = "SenhaForte!1";

        assertTrue(PasswordPolicy.isValid(senha));
        assertNull(PasswordPolicy.violationMessage(senha));
    }

    @Test
    @DisplayName("Senha curta informa violações de comprimento e formato")
    void senhaInvalidaPorComprimentoEFormato() {
        String senha = "abc";

        assertFalse(PasswordPolicy.isValid(senha));
        String mensagem = PasswordPolicy.violationMessage(senha);

        assertNotNull(mensagem);
        assertTrue(mensagem.contains("Mínimo de 10 caracteres."));
        assertTrue(mensagem.contains("Pelo menos 1 letra maiúscula (A-Z)."));
        assertTrue(mensagem.contains("Pelo menos 1 caractere especial (ex.: !@#$%&*)."));
    }

    @Test
    @DisplayName("Senha nula retorna indicação de senha vazia")
    void senhaNula() {
        String mensagem = PasswordPolicy.violationMessage(null);

        assertNotNull(mensagem);
        assertTrue(mensagem.contains("Senha vazia."));
        assertFalse(PasswordPolicy.isValid(null));
    }
}
