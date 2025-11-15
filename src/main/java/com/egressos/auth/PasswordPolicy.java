package com.egressos.auth;

import java.util.ArrayList;
import java.util.List;

public class PasswordPolicy {

    private static final int MIN_LEN = 10;

    public static boolean isValid(String senha) {
        return violations(senha).isEmpty();
    }

    public static String violationMessage(String senha) {
        List<String> viols = violations(senha);
        if (viols.isEmpty()) return null;
        return "A senha não atende aos requisitos:\n- " + String.join("\n- ", viols);
    }

    private static List<String> violations(String senha) {
        List<String> v = new ArrayList<>();
        if (senha == null || senha.isEmpty()) {
            v.add("Senha vazia.");
            return v;
        }
        if (senha.length() < MIN_LEN) v.add("Mínimo de 10 caracteres.");
        boolean maiuscula = false;
        boolean minuscula = false;
        boolean especial = false;
        for (char c : senha.toCharArray()) {
            if (Character.isUpperCase(c)) maiuscula = true;
            else if (Character.isLowerCase(c)) minuscula = true;
            else if (!Character.isDigit(c)) especial = true;
        }
        if (!maiuscula) v.add("Pelo menos 1 letra maiúscula (A-Z).");
        if (!minuscula) v.add("Pelo menos 1 letra minúscula (a-z).");
        if (!especial) v.add("Pelo menos 1 caractere especial (ex.: !@#$%&*).");
        return v;
    }
}
