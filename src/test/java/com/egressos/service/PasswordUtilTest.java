package com.egressos.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    @DisplayName("Hash SHA-256 é determinístico e em hexadecimal")
    void hashDeterministico() {
        String senha = "SenhaSegura!";

        String hash1 = PasswordUtil.hash(senha);
        String hash2 = PasswordUtil.hash(senha);

        assertNotNull(hash1);
        assertEquals(hash1, hash2);
        assertEquals(64, hash1.length());
        assertTrue(hash1.matches("[0-9a-f]{64}"));
    }
}
