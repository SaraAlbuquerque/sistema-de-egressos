package br.com.egressos.domain;

public class PasswordPolicyService {
    public void validar(String senha) {
        if (senha == null || senha.length() < 8)
            throw new IllegalArgumentException("Senha deve ter ao menos 8 caracteres");
        if (!senha.matches(".*[A-Z].*") || !senha.matches(".*[a-z].*") || !senha.matches(".*\\d.*"))
            throw new IllegalArgumentException("Senha deve conter maiúsculas, minúsculas e dígitos");
    }
}
