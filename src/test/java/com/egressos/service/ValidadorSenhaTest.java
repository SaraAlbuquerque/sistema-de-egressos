package com.egressos.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidadorSenhaTest {

    private final ValidadorSenha validador = new ValidadorSenha();

    @Test
    @DisplayName("Validação de login compara o hash da senha informada")
    void validarSenhaLogin() {
        String senha = "SenhaSegura#1";
        String hashArmazenado = PasswordUtil.hash(senha);

        assertTrue(validador.validarSenhaLogin(senha, hashArmazenado));
        assertFalse(validador.validarSenhaLogin("outraSenha", hashArmazenado));
        assertFalse(validador.validarSenhaLogin("", hashArmazenado));
    }

    @Test
    @DisplayName("Força de senha exige comprimento e variedade de caracteres")
    void validarForcaSenha() {
        assertTrue(validador.validarForcaSenha("NovaSenha!1"));

        assertFalse(validador.validarForcaSenha("curta!"));
        assertFalse(validador.validarForcaSenha("somemaiusculas"));
        assertFalse(validador.validarForcaSenha(null));
    }
}
