package com.egressos.view;

import com.egressos.auth.AuthService;
import com.egressos.model.Usuario;
import com.egressos.service.PasswordResetService;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class ChangePasswordDialog extends JDialog {

    private final Usuario usuario;
    private final AuthService auth = new AuthService();
    private final PasswordResetService resetService = new PasswordResetService();

    private final JPasswordField senhaAtualField   = new JPasswordField();
    private final JPasswordField novaSenhaField    = new JPasswordField();
    private final JPasswordField confSenhaField    = new JPasswordField();

    public ChangePasswordDialog(Frame owner, Usuario usuario) {
        super(owner, "Alterar senha", true);
        this.usuario = usuario;

        setSize(420, 260);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        int y = 0;

        c.gridx = 0; c.gridy = y;
        form.add(new JLabel("Senha atual:"), c);
        c.gridx = 1;
        form.add(senhaAtualField, c);
        y++;

        c.gridx = 0; c.gridy = y;
        form.add(new JLabel("Nova senha:"), c);
        c.gridx = 1;
        form.add(novaSenhaField, c);
        y++;

        c.gridx = 0; c.gridy = y;
        form.add(new JLabel("Confirmar nova senha:"), c);
        c.gridx = 1;
        form.add(confSenhaField, c);
        y++;

        add(form, BorderLayout.CENTER);

        JButton cancelarBtn = new JButton("Cancelar");
        JButton salvarBtn   = new JButton("Salvar");

        cancelarBtn.addActionListener(e -> dispose());
        salvarBtn.addActionListener(e -> salvar());

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(cancelarBtn);
        south.add(salvarBtn);

        add(south, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(salvarBtn);
    }

    private void salvar() {
        String senhaAtual = new String(senhaAtualField.getPassword());
        String nova       = new String(novaSenhaField.getPassword());
        String conf       = new String(confSenhaField.getPassword());

        if (senhaAtual.isBlank() || nova.isBlank() || conf.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Preencha todos os campos.");
            return;
        }

        if (!nova.equals(conf)) {
            JOptionPane.showMessageDialog(this,
                    "A nova senha e a confirmação não coincidem.");
            return;
        }


        Optional<Usuario> ok = auth.login(usuario.getEmail(), senhaAtual);
        if (ok.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Senha atual incorreta.");
            return;
        }

        try {
            var tokenOpt = resetService.iniciar(usuario.getEmail());
            if (tokenOpt.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Não foi possível iniciar a troca de senha.");
                return;
            }
            String token = tokenOpt.get();
            boolean sucesso = resetService.concluir(token, nova);

            if (sucesso) {
                JOptionPane.showMessageDialog(this,
                        "Senha alterada com sucesso.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Falha ao alterar senha (token inválido/expirado).");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao alterar senha: " + ex.getMessage());
        }
    }
}
