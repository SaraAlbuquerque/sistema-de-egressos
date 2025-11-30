package com.egressos.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordsTest {

    @Test
    @DisplayName("Geração de senha temporária retorna valor curto e não nulo")
    void randomTempPassword() {
        String primeira = Passwords.randomTempPassword();
        String segunda = Passwords.randomTempPassword();

        assertNotNull(primeira);
        assertNotNull(segunda);
        assertEquals(8, primeira.length());
        assertNotEquals(primeira, segunda);
    }

    @Test
    @DisplayName("Hash SHA-256 é determinístico para a mesma entrada")
    void sha256() {
        String entrada = "SenhaSegura";

        String hash1 = Passwords.sha256(entrada);
        String hash2 = Passwords.sha256(entrada);

        assertEquals(hash1, hash2);
        assertEquals(44, hash1.length());
    }
}
