package com.egressos.ui;

import com.egressos.auth.AuthService;
import com.egressos.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class LoginFrame extends JFrame {

    private final AuthService auth = new AuthService();

    private JTextField emailField;
    private JPasswordField senhaField;
    private JButton entrarBtn;
    private JButton criarContaBtn;
    private JButton esqueciSenhaBtn;

    public LoginFrame() {
        super("Egressos — Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(480, 280);
        setLocationRelativeTo(null);
        buildUI();
        wireEvents();
    }

    private void buildUI() {
        JPanel root = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        JLabel titulo = new JLabel("Acesso ao Sistema de Egressos", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 16f));
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2; c.fill = GridBagConstraints.HORIZONTAL;
        root.add(titulo, c);

        c.gridwidth = 1; c.gridy++;
        root.add(new JLabel("E-mail:"), c);
        emailField = new JTextField();
        c.gridx = 1;
        root.add(emailField, c);

        c.gridx = 0; c.gridy++;
        root.add(new JLabel("Senha:"), c);
        senhaField = new JPasswordField();
        c.gridx = 1;
        root.add(senhaField, c);

        c.gridx = 0; c.gridy++; c.gridwidth = 1;
        criarContaBtn = new JButton("Quero uma conta");
        root.add(criarContaBtn, c);

        c.gridx = 1;
        esqueciSenhaBtn = new JButton("Esqueci minha senha");
        root.add(esqueciSenhaBtn, c);

        c.gridx = 0; c.gridy++; c.gridwidth = 2;
        entrarBtn = new JButton("Entrar");
        root.add(entrarBtn, c);

        setContentPane(root);
        getRootPane().setDefaultButton(entrarBtn);
    }

    private void wireEvents() {
        entrarBtn.addActionListener(e -> tentarLogin());
        esqueciSenhaBtn.addActionListener(e -> abrirEsqueciSenha());
        criarContaBtn.addActionListener(e -> abrirSolicitarConta());
        senhaField.addActionListener(e -> tentarLogin());
    }

    private void tentarLogin() {
        String email = emailField.getText().trim();
        String senha = new String(senhaField.getPassword());

        if (email.isBlank() || senha.isBlank()) {
            JOptionPane.showMessageDialog(this, "Informe e-mail e senha.");
            return;
        }

        Optional<Usuario> uOpt = auth.login(email, senha);
        if (uOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Credenciais inválidas.");
            return;
        }

        Usuario u = uOpt.get();
        auth.setUsuarioLogado(u);

        dispose();
        switch (u.getPapel()) {
            case COORDENADOR -> new CoordinatorFrame(u).setVisible(true);
            case EGRESSO -> new EgressoFrame(u).setVisible(true);
            default -> new DocenteFrame(u).setVisible(true);
        }
    }

    private void abrirSolicitarConta() {
        new SignUpDialog(this).setVisible(true);
    }

    private void abrirEsqueciSenha() {
        new ForgotPasswordDialog(this).setVisible(true);
    }
}
