package com.egressos.ui;

import com.egressos.dao.SolicitacoesDao;
import com.egressos.model.Papel;
import com.egressos.model.Usuario;
import com.egressos.service.UsersService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CoordinatorFrame extends JFrame {
    private final UsersService usersService = new UsersService();
    private final SolicitacoesDao solicitacoesDao = new SolicitacoesDao();

    private JTextField emailField;
    private JTextField nomeField;
    private JComboBox<Papel> papelCombo;
    private JButton criarBtn;
    private JTable usersTable;
    private DefaultTableModel usersModel;

    private JTable solicitacoesTable;
    private DefaultTableModel solicitacoesModel;
    private JButton aprovarBtn;
    private JButton rejeitarBtn;

    public CoordinatorFrame(Usuario coord) {
        super("Egressos — Painel do Coordenador");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);

        setJMenuBar(buildMenu(coord));
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Usuários", buildUsersTab());
        tabs.add("Solicitações", buildSolicitacoesTab());
        tabs.add("Relatórios", buildReportsTab());
        setContentPane(tabs);

        refreshUsers();
        refreshSolicitacoes();
    }
    
    private JPanel buildReportsTab() {
        JPanel panel = new JPanel(new GridLayout(7, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JButton btnEmprego = new JButton("Exportar — Status de Emprego (CSV)");
        JButton btnEscolaridade = new JButton("Exportar — Escolaridade (CSV)");
        JButton btnAreas = new JButton("Exportar — Áreas de Atuação (CSV)");
        JButton btnLocais = new JButton("Exportar — Locais (CSV)");
        JButton btnCrescimento = new JButton("Exportar — Crescimento na Carreira (CSV)");
        JButton btnEventosTipo = new JButton("Exportar — Eventos por Tipo (CSV)");
        JButton btnEventosOrg  = new JButton("Exportar — Eventos por Organização (CSV)");

        panel.add(btnEmprego);
        panel.add(btnEscolaridade);
        panel.add(btnAreas);
        panel.add(btnLocais);
        panel.add(btnCrescimento);
        panel.add(btnEventosTipo);
        panel.add(btnEventosOrg);

        com.egressos.service.ReportsService reports = new com.egressos.service.ReportsService();

        btnEmprego.addActionListener(e -> exportarCsv("emprego.csv", p -> reports.exportEmpregoCsv(p)));
        btnEscolaridade.addActionListener(e -> exportarCsv("escolaridade.csv", p -> reports.exportEscolaridadeCsv(p)));
        btnAreas.addActionListener(e -> exportarCsv("areas.csv", p -> reports.exportAreasCsv(p)));
        btnLocais.addActionListener(e -> exportarCsv("locais.csv", p -> reports.exportLocaisCsv(p)));
        btnCrescimento.addActionListener(e -> exportarCsv("crescimento.csv", p -> reports.exportCrescimentoCsv(p)));
        btnEventosTipo.addActionListener(e -> exportarCsv("eventos_por_tipo.csv", p -> reports.exportEventosPorTipoCsv(p)));
        btnEventosOrg.addActionListener(e -> exportarCsv("eventos_por_organizacao.csv", p -> reports.exportEventosPorOrganizacaoCsv(p)));

        return panel;
    }

    @FunctionalInterface
    private interface ExportFn { java.nio.file.Path run(java.nio.file.Path destino) throws Exception; }

    private void exportarCsv(String defaultName, ExportFn fn) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File(defaultName));
        int r = chooser.showSaveDialog(this);
        if (r == JFileChooser.APPROVE_OPTION) {
            try {
                java.nio.file.Path out = chooser.getSelectedFile().toPath();
                fn.run(out);
                JOptionPane.showMessageDialog(this, "Arquivo gerado:\n" + out.toAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao exportar: " + ex.getMessage());
            }
        }
    }

    private JMenuBar buildMenu(Usuario coord) {
        JMenuBar bar = new JMenuBar();
        JMenu arquivo = new JMenu("Arquivo");

        JMenuItem trocar = new JMenuItem("Trocar de usuário");
        trocar.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        JMenuItem sair = new JMenuItem("Sair");
        sair.addActionListener(e -> System.exit(0));

        JMenuItem sobre = new JMenuItem("Sobre");
        sobre.addActionListener(e -> JOptionPane.showMessageDialog(this,
                """
                Sistema de Egressos \u2014 Coordena\u00e7\u00e3o
                Usu\u00e1rio: """ + coord.getNome() + " (" + coord.getEmail() + ")\n" +
                        "Versão: 1.0"));

        arquivo.add(trocar);
        arquivo.addSeparator();
        arquivo.add(sobre);
        arquivo.addSeparator();
        arquivo.add(sair);
        bar.add(arquivo);

        return bar;
    }

    private JPanel buildUsersTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        int y = 0;
        c.gridx = 0; c.gridy = y; form.add(new JLabel("E-mail:"), c);
        emailField = new JTextField();
        c.gridx = 1; form.add(emailField, c);

        y++;
        c.gridx = 0; c.gridy = y; form.add(new JLabel("Nome:"), c);
        nomeField = new JTextField();
        c.gridx = 1; form.add(nomeField, c);

        y++;
        c.gridx = 0; c.gridy = y; form.add(new JLabel("Papel:"), c);
        papelCombo = new JComboBox<>(new Papel[]{Papel.EGRESSO, Papel.DOCENTE});
        c.gridx = 1; form.add(papelCombo, c);

        y++;
        criarBtn = new JButton("Criar usuário");
        c.gridx = 0; c.gridy = y; c.gridwidth = 2;
        form.add(criarBtn, c);

        panel.add(form, BorderLayout.NORTH);

        usersModel = new DefaultTableModel(new Object[]{"ID", "Nome", "E-mail", "Papel"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        usersTable = new JTable(usersModel);
        usersTable.setAutoCreateRowSorter(true);
        panel.add(new JScrollPane(usersTable), BorderLayout.CENTER);

        criarBtn.addActionListener(e -> criarUsuario());

        return panel;
    }

    private void criarUsuario() {
        String email = emailField.getText().trim();
        String nome = nomeField.getText().trim();
        Papel papel = (Papel) papelCombo.getSelectedItem();

        if (email.isBlank() || nome.isBlank()) {
            JOptionPane.showMessageDialog(this, "Informe e-mail e nome.");
            return;
        }

        try {
            var u = usersService.criarUsuario(email, nome, papel);
            JOptionPane.showMessageDialog(this,
                    """
                    Usu\u00e1rio criado com sucesso!
                    
                    E-mail: """ + email + "\n" +
                            "Papel: " + papel.name() + "\n" +
                            "Senha temporária: " + u.getSenhaHash() + "\n\n" +
                            "O usuário pode acessar o sistema com essa senha.");
            emailField.setText("");
            nomeField.setText("");
            refreshUsers();
        } catch (HeadlessException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void refreshUsers() {
        try {
            List<com.egressos.model.Usuario> lista = usersService.listar();
            usersModel.setRowCount(0);
            for (var u : lista) {
                usersModel.addRow(new Object[]{u.getId(), u.getNome(), u.getEmail(), u.getPapel().name()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao listar usuários: " + ex.getMessage());
        }
    }

    private JPanel buildSolicitacoesTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        solicitacoesModel = new DefaultTableModel(new Object[]{"ID", "Nome", "E-mail", "Papel"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        solicitacoesTable = new JTable(solicitacoesModel);
        solicitacoesTable.setAutoCreateRowSorter(true);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        aprovarBtn = new JButton("Aprovar");
        rejeitarBtn = new JButton("Rejeitar");
        buttons.add(aprovarBtn);
        buttons.add(rejeitarBtn);

        panel.add(new JScrollPane(solicitacoesTable), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);

        aprovarBtn.addActionListener(e -> aprovarSolicitacao());
        rejeitarBtn.addActionListener(e -> rejeitarSolicitacao());

        return panel;
    }

    private void refreshSolicitacoes() {
        try {
            solicitacoesModel.setRowCount(0);
            for (String[] r : solicitacoesDao.listar()) {
                solicitacoesModel.addRow(new Object[]{r[0], r[1], r[2], r[3]});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao listar solicitações: " + ex.getMessage());
        }
    }

    private void aprovarSolicitacao() {
        int row = solicitacoesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma solicitação para aprovar.");
            return;
        }

        String id = (String) solicitacoesModel.getValueAt(row, 0);
        String nome = (String) solicitacoesModel.getValueAt(row, 1);
        String email = (String) solicitacoesModel.getValueAt(row, 2);
        String papelStr = (String) solicitacoesModel.getValueAt(row, 3);
        Papel papel = Papel.valueOf(papelStr);

        try {
            var u = usersService.criarUsuario(email, nome, papel);
            solicitacoesDao.remover(id);
            JOptionPane.showMessageDialog(this,
                    """
                    Solicita\u00e7\u00e3o aprovada!
                    
                    Usu\u00e1rio: """ + nome + "\n" +
                            "E-mail: " + email + "\n" +
                            "Papel: " + papel.name() + "\n" +
                            "Senha temporária: " + u.getSenhaHash() + "\n\n" +
                            "O usuário pode entrar no sistema com essa senha.");
            refreshSolicitacoes();
            refreshUsers();
        } catch (HeadlessException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao aprovar solicitação:\n" + ex.getMessage());
        }
    }

    private void rejeitarSolicitacao() {
        int row = solicitacoesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma solicitação para rejeitar.");
            return;
        }

        String id = (String) solicitacoesModel.getValueAt(row, 0);
        try {
            solicitacoesDao.remover(id);
            refreshSolicitacoes();
            JOptionPane.showMessageDialog(this, "Solicitação rejeitada com sucesso.");
        } catch (HeadlessException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao rejeitar solicitação: " + ex.getMessage());
        }
    }
}
