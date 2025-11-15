package com.egressos.service;

public class ValidadorSenha {

    public boolean validarSenhaLogin(String senhaDigitada, String senhaArmazenadaHash) {
        if (senhaDigitada == null || senhaDigitada.isEmpty()) return false;
        String hash = PasswordUtil.hash(senhaDigitada);
        return hash.equals(senhaArmazenadaHash);
    }

    public boolean validarForcaSenha(String novaSenha) {
        if (novaSenha == null) return false;
        if (novaSenha.length() < 10) return false;
        boolean temMaiuscula = false;
        boolean temMinuscula = false;
        boolean temDigito = false;
        boolean temEspecial = false;
        for (char c : novaSenha.toCharArray()) {
            if (Character.isUpperCase(c)) temMaiuscula = true;
            else if (Character.isLowerCase(c)) temMinuscula = true;
            else if (Character.isDigit(c)) temDigito = true;
            else temEspecial = true;
        }
        return temMaiuscula && temMinuscula && temEspecial;
    }
}
