package br.com.egressos.application;
import java.security.SecureRandom;
public class PasswordGenerator {
    private static final String ALPH = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
    public static String generate() {
        SecureRandom r = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<10;i++) sb.append(ALPH.charAt(r.nextInt(ALPH.length())));
        return sb.toString();
    }
}
