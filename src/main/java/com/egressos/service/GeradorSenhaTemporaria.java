package com.egressos.service;

import java.security.SecureRandom;

public class GeradorSenhaTemporaria {

    private static final String ALFABETO = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public String gerarSenha() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int idx = random.nextInt(ALFABETO.length());
            sb.append(ALFABETO.charAt(idx));
        }
        return sb.toString();
    }
}
