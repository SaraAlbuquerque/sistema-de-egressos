package com.egressos.auth;

import java.util.ArrayList;
import java.util.List;

public class PasswordPolicy {
    private static final int MIN_LEN = 10;

    public static boolean isValid(String pwd) {
        return violations(pwd).isEmpty();
    }

    public static String violationMessage(String pwd) {
        List<String> v = violations(pwd);
        if (v.isEmpty()) return null;
        return "A senha não atende aos requisitos:\n- " + String.join("\n- ", v);
    }

    private static List<String> violations(String pwd) {
        List<String> v = new ArrayList<>();
        if (pwd == null) {
            v.add("Senha vazia.");
            return v;
        }
        if (pwd.length() < MIN_LEN) v.add("Mínimo de 10 caracteres.");
        if (!pwd.chars().anyMatch(ch -> Character.isUpperCase(ch))) v.add("Pelo menos 1 letra maiúscula (A-Z).");
        if (!pwd.chars().anyMatch(ch -> Character.isLowerCase(ch))) v.add("Pelo menos 1 letra minúscula (a-z).");
        if (pwd.chars().allMatch(ch -> Character.isLetterOrDigit(ch))) v.add("Pelo menos 1 caractere especial (ex.: !@#$%&*).");
        return v;
    }
}
