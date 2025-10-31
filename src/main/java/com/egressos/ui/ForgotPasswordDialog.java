package com.egressos.ui;

import com.egressos.service.PasswordResetService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ForgotPasswordDialog extends JDialog {
    private final PasswordResetService svc = new PasswordResetService();

    public ForgotPasswordDialog(Frame owner) {
        super(owner, "Recuperar senha", true);
        setSize(420, 240); setLocationRelativeTo(owner);

        JTextField email = new JTextField();
        JButton gerar = new JButton("Gerar token");
        JTextField token = new JTextField();
        JPasswordField nova = new JPasswordField();
        JButton resetar = new JButton("Redefinir");

        setLayout(new GridLayout(5,2,6,6));
        add(new JLabel("Seu e-mail:")); add(email);
        add(new JLabel()); add(gerar);
        add(new JLabel("Token recebido:")); add(token);
        add(new JLabel("Nova senha:")); add(nova);
        add(new JLabel()); add(resetar);

        gerar.addActionListener(e -> {
            var t = svc.iniciar(email.getText().trim());
            if (t.isEmpty()) JOptionPane.showMessageDialog(this, "E-mail não encontrado.");
            else {
                JOptionPane.showMessageDialog(this, "Token gerado (válido por 15 min):\n" + t.get());
                token.setText(t.get());
            }
        });

        resetar.addActionListener((ActionEvent e) -> {
            try {
                boolean ok = svc.concluir(token.getText().trim(), new String(nova.getPassword()));
                JOptionPane.showMessageDialog(this, ok ? "Senha atualizada!" : "Token inválido/expirado.");
                if (ok) dispose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            } catch (HeadlessException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao redefinir senha: " + ex.getMessage());
            }
        });
    }
}
