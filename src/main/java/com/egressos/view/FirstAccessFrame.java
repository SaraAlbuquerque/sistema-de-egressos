package com.egressos.view;

import com.egressos.auth.AuthService;
import com.egressos.controller.EgressoController;
import com.egressos.model.Usuario;
import com.egressos.util.DatasBR;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.time.LocalDate;

public class FirstAccessFrame extends JFrame {

    private final Usuario usuario;
    private final AuthService authService = new AuthService();
    private final EgressoController egressoController = new EgressoController();

    private JPasswordField senhaAtualField;
    private JPasswordField novaSenhaField;
    private JPasswordField confirmaSenhaField;
    private JTextField dataNascimentoField;
    private JTextField anoConclusaoField;
    private JTextField githubField;
    private JTextField rede1Field;
    private JTextField rede2Field;

    public FirstAccessFrame(Usuario usuario) {
        super("Primeiro acesso do egresso");
        this.usuario = usuario;
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(buildContent());
    }

    private JPanel buildContent() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        int y = 0;

        senhaAtualField     = new JPasswordField();
        novaSenhaField      = new JPasswordField();
        confirmaSenhaField  = new JPasswordField();
        dataNascimentoField = new JTextField();
        anoConclusaoField   = new JTextField();
        githubField         = new JTextField();
        rede1Field          = new JTextField();
        rede2Field          = new JTextField();

        aplicarMascaraDataBR(dataNascimentoField);
        aplicarMascaraAnoQuatroDigitos(anoConclusaoField);

        c.gridx = 0;
        c.gridy = y;
        form.add(new JLabel("Senha atual:"), c);
        c.gridx = 1;
        form.add(senhaAtualField, c);
        y++;

        c.gridx = 0;
        c.gridy = y;
        form.add(new JLabel("Nova senha:"), c);
        c.gridx = 1;
        form.add(novaSenhaField, c);
        y++;

        c.gridx = 0;
        c.gridy = y;
        form.add(new JLabel("Confirmar nova senha:"), c);
        c.gridx = 1;
        form.add(confirmaSenhaField, c);
        y++;

        c.gridx = 0;
        c.gridy = y;
        form.add(new JLabel("Data de nascimento:"), c);
        c.gridx = 1;
        form.add(dataNascimentoField, c);
        y++;

        c.gridx = 0;
        c.gridy = y;
        form.add(new JLabel("Ano de conclusão:"), c);
        c.gridx = 1;
        form.add(anoConclusaoField, c);
        y++;

        c.gridx = 0;
        c.gridy = y;
        form.add(new JLabel("Usuário do GitHub:"), c);
        c.gridx = 1;
        form.add(githubField, c);
        y++;

        c.gridx = 0;
        c.gridy = y;
        form.add(new JLabel("Rede social 1 (opcional):"), c);
        c.gridx = 1;
        form.add(rede1Field, c);
        y++;

        c.gridx = 0;
        c.gridy = y;
        form.add(new JLabel("Rede social 2 (opcional):"), c);
        c.gridx = 1;
        form.add(rede2Field, c);

        panel.add(form, BorderLayout.CENTER);

        JButton salvar = new JButton("Concluir");
        salvar.addActionListener(e -> concluirPrimeiroAcesso());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(salvar);
        panel.add(south, BorderLayout.SOUTH);

        return panel;
    }

    private void concluirPrimeiroAcesso() {
        String senhaAtual = new String(senhaAtualField.getPassword());
        String novaSenha  = new String(novaSenhaField.getPassword());
        String confirma   = new String(confirmaSenhaField.getPassword());

        if (!novaSenha.equals(confirma)) {
            JOptionPane.showMessageDialog(this, "A nova senha e a confirmação não conferem.");
            return;
        }

        AuthService.ResultadoTrocaObrigatoria r =
                authService.trocarSenhaObrigatoria(usuario, senhaAtual, novaSenha);

        if (r == AuthService.ResultadoTrocaObrigatoria.SENHA_ANTIGA_INCORRETA) {
            JOptionPane.showMessageDialog(this, "Senha atual incorreta.");
            return;
        }
        if (r == AuthService.ResultadoTrocaObrigatoria.NOVA_SENHA_FRACA) {
            JOptionPane.showMessageDialog(this, "Nova senha não atende aos critérios.");
            return;
        }

        String sData = dataNascimentoField.getText().trim();
        LocalDate dataNasc = DatasBR.parse(sData);
        if (dataNasc == null) {
            JOptionPane.showMessageDialog(this,
                    "Data de nascimento inválida.");
            return;
        }

        String sAno = anoConclusaoField.getText().trim();
        if (!sAno.matches("\\d{4}")) {
            JOptionPane.showMessageDialog(this,
                    "Ano de conclusão inválido..");
            return;
        }
        Integer anoConc;
        try {
            anoConc = Integer.parseInt(sAno);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ano de conclusão inválido.");
            return;
        }

        String github = githubField.getText().trim();
        if (github.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Usuário do GitHub é obrigatório.");
            return;
        }

        boolean okPerfil = egressoController.completarCadastro(
                usuario.getId(),
                dataNasc,
                anoConc,
                github,
                rede1Field.getText().trim(),
                rede2Field.getText().trim()
        );
        if (!okPerfil) {
            JOptionPane.showMessageDialog(this, "Falha ao completar cadastro.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Cadastro concluído com sucesso.");
        dispose();
        new EgressoFrame(usuario).setVisible(true);
    }


    private void aplicarMascaraDataBR(JTextField campo) {
        AbstractDocument doc = (AbstractDocument) campo.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {

                String valorAtual = fb.getDocument().getText(0, fb.getDocument().getLength());
                String novoValor  = valorAtual.substring(0, offset) + text + valorAtual.substring(offset + length);

                novoValor = novoValor.replaceAll("[^0-9]", ""); // só números

                if (novoValor.length() > 8) return; // ddMMyyyy

                StringBuilder out = new StringBuilder();
                for (int i = 0; i < novoValor.length(); i++) {
                    out.append(novoValor.charAt(i));
                    if (i == 1 || i == 3) out.append('/'); // dd/MM/yyyy
                }

                fb.remove(0, fb.getDocument().getLength());
                fb.insertString(0, out.toString(), attrs);
            }
        });
    }


    private void aplicarMascaraAnoQuatroDigitos(JTextField campo) {
        AbstractDocument doc = (AbstractDocument) campo.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length,
                                String text, AttributeSet attrs) throws BadLocationException {

                String valorAtual = fb.getDocument().getText(0, fb.getDocument().getLength());
                String novoValor  = valorAtual.substring(0, offset) + text + valorAtual.substring(offset + length);

                novoValor = novoValor.replaceAll("[^0-9]", ""); // só números

                if (novoValor.length() > 4) return; // AAAA

                fb.remove(0, fb.getDocument().getLength());
                fb.insertString(0, novoValor, attrs);
            }
        });
    }
}
