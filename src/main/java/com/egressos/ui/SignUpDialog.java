package com.egressos.ui;

import com.egressos.dao.SolicitacoesDao;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import java.util.UUID;

public class SignUpDialog extends JDialog {
    public SignUpDialog(Frame owner) {
        super(owner, "Solicitar conta", true);
        setSize(420, 260); setLocationRelativeTo(owner);

        JTextField nome = new JTextField();
        JTextField email = new JTextField();
        JComboBox<String> papel = new JComboBox<>(new String[]{"EGRESSO", "DOCENTE"});
        JButton enviar = new JButton("Enviar solicitação");

        setLayout(new GridLayout(4,2,6,6));
        add(new JLabel("Nome:")); add(nome);
        add(new JLabel("E-mail:")); add(email);
        add(new JLabel("Perfil:")); add(papel);
        add(new JLabel()); add(enviar);

        enviar.addActionListener(ev -> {
            if (nome.getText().isBlank() || email.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Preencha nome e e-mail.");
                return;
            }
            new SolicitacoesDao().criar(
                    UUID.randomUUID().toString(),
                    nome.getText().trim(),
                    email.getText().trim(),
                    papel.getSelectedItem().toString(),
                    Instant.now()
            );
            JOptionPane.showMessageDialog(this, "Solicitação enviada! Aguarde aprovação do Coordenador.");
            dispose();
        });
    }
}
