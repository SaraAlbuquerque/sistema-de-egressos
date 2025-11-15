package com.egressos.ui;

import com.egressos.controller.RelatorioController;
import com.egressos.dao.SolicitacoesDao;
import com.egressos.dao.UsuariosDao;
import com.egressos.model.Papel;
import com.egressos.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CoordinatorFrame extends JFrame {
    private final RelatorioController relatorioController = new RelatorioController();
    private final UsuariosDao usuariosDao = new UsuariosDao();
    private final SolicitacoesDao solicitacoesDao = new SolicitacoesDao();
    private JTabbedPane tabs;
    private Usuario usuarioAtual;

    public CoordinatorFrame() {
        super("Área do Coordenador");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        tabs = new JTabbedPane();
        tabs.add("Usuários", buildUsersTab());
        tabs.add("Solicitações", buildRequestsTab());
        tabs.add("Relatórios", buildReportsTab());
        tabs.add("Eventos", new CoordinatorEventosPanel());
        setContentPane(tabs);
    }

    public CoordinatorFrame(Usuario usuario) {
        this();
        this.usuarioAtual = usuario;
    }

    private JPanel buildRequestsTab() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Nome", "E-mail", "Papel", "Criado em"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        root.add(new JScrollPane(table), BorderLayout.CENTER);
        JButton aprovar = new JButton("Aprovar");
        JButton remover = new JButton("Remover");
        JButton recarregar = new JButton("Recarregar");
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(recarregar);
        south.add(remover);
        south.add(aprovar);
        root.add(south, BorderLayout.SOUTH);
        Runnable loader = () -> {
            model.setRowCount(0);
            for (String[] r : solicitacoesDao.listar()) {
                model.addRow(new Object[]{r[0], r[1], r[2], r[3], r[4]});
            }
        };
        recarregar.addActionListener(e -> loader.run());
        remover.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Selecione uma solicitação.");
                return;
            }
            int m = table.convertRowIndexToModel(row);
            String id = String.valueOf(model.getValueAt(m, 0));
            solicitacoesDao.remover(id);
            loader.run();
        });
        aprovar.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Selecione uma solicitação.");
                return;
            }
            int m = table.convertRowIndexToModel(row);
            String id = String.valueOf(model.getValueAt(m, 0));
            String nome = String.valueOf(model.getValueAt(m, 1));
            String email = String.valueOf(model.getValueAt(m, 2));
            String papelStr = String.valueOf(model.getValueAt(m, 3));
            Papel papel;
            try {
                papel = Papel.valueOf(papelStr.toUpperCase());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Papel inválido: " + papelStr);
                return;
            }
            Usuario u = new Usuario(
                    UUID.randomUUID().toString(),
                    email,
                    nome,
                    papel,
                    "",
                    true,
                    Instant.now()
            );
            usuariosDao.salvarOuAtualizar(u);
            solicitacoesDao.remover(id);
            JOptionPane.showMessageDialog(this, "Solicitação aprovada. O usuário deve redefinir a senha no primeiro acesso.");
            loader.run();
        });
        loader.run();
        return root;
    }

    private JPanel buildReportsTab() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JButton btnEventosTipoCsv = new JButton("Exportar — Eventos por Tipo (CSV)");
        JButton btnEventosOrgCsv = new JButton("Exportar — Eventos por Organização (CSV)");
        JTextField de = new JTextField();
        de.setToolTipText("YYYY-MM-DD");
        JTextField ate = new JTextField();
        ate.setToolTipText("YYYY-MM-DD");
        JTextField local = new JTextField();
        local.setToolTipText("Trecho do local");
        JButton btnPeriodoCsv = new JButton("Exportar — Eventos por Período (CSV)");
        JButton btnLocalCsv = new JButton("Exportar — Eventos por Local (CSV)");
        JButton btnEventosBasicoPdf = new JButton("Exportar — Eventos (PDF)");
        panel.add(btnEventosTipoCsv);
        panel.add(btnEventosOrgCsv);
        panel.add(new JLabel("Período (opcional): de / até"));
        JPanel p1 = new JPanel(new GridLayout(1, 2, 8, 8));
        p1.add(de);
        p1.add(ate);
        panel.add(p1);
        panel.add(btnPeriodoCsv);
        panel.add(new JLabel("Filtro de Local (contém):"));
        panel.add(local);
        panel.add(btnLocalCsv);
        panel.add(btnEventosBasicoPdf);
        btnEventosTipoCsv.addActionListener(e -> exportarCsv(
                "eventos_por_tipo.csv",
                RelatorioController.TipoRelatorio.EVENTOS_POR_TIPO,
                null,
                null,
                null
        ));
        btnEventosOrgCsv.addActionListener(e -> exportarCsv(
                "eventos_por_organizacao.csv",
                RelatorioController.TipoRelatorio.EVENTOS_POR_ORGANIZACAO,
                null,
                null,
                null
        ));
        btnPeriodoCsv.addActionListener(e -> {
            LocalDate ini = parseDate(de.getText());
            LocalDate fim = parseDate(ate.getText());
            exportarCsv(
                    "eventos_periodo.csv",
                    RelatorioController.TipoRelatorio.EVENTOS_POR_PERIODO,
                    ini,
                    fim,
                    null
            );
        });
        btnLocalCsv.addActionListener(e -> {
            String filtro = local.getText();
            exportarCsv(
                    "eventos_local.csv",
                    RelatorioController.TipoRelatorio.EVENTOS_POR_LOCAL,
                    null,
                    null,
                    filtro
            );
        });
        btnEventosBasicoPdf.addActionListener(e -> exportarPdf(
                "eventos.pdf",
                RelatorioController.TipoRelatorio.EVENTOS_BASICO
        ));
        return panel;
    }

    private void exportarCsv(String nome,
                             RelatorioController.TipoRelatorio tipo,
                             LocalDate ini,
                             LocalDate fim,
                             String filtroLocal) {
        java.io.File f = escolherArquivo(nome);
        if (f == null) return;
        try {
            relatorioController.gerarCsv(tipo, f.toPath(), ini, fim, filtroLocal);
            JOptionPane.showMessageDialog(this, "Gerado: " + f.getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Falha: " + ex.getMessage());
        }
    }

    private void exportarPdf(String nome,
                             RelatorioController.TipoRelatorio tipo) {
        java.io.File f = escolherArquivo(nome);
        if (f == null) return;
        try {
            relatorioController.gerarPdf(tipo, f.toPath());
            JOptionPane.showMessageDialog(this, "Gerado: " + f.getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Falha: " + ex.getMessage());
        }
    }

    private java.io.File escolherArquivo(String suggest) {
        javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
        fc.setSelectedFile(new java.io.File(suggest));
        int r = fc.showSaveDialog(this);
        return r == javax.swing.JFileChooser.APPROVE_OPTION ? fc.getSelectedFile() : null;
    }

    private static LocalDate parseDate(String s) {
        try {
            s = s == null ? "" : s.trim();
            if (s.isEmpty()) return null;
            return LocalDate.parse(s);
        } catch (Exception ex) {
            return null;
        }
    }
    
    private void abrirDialogNovoUsuario(Runnable onCreated) {
        JTextField nome = new JTextField();
        JTextField email = new JTextField();
        JComboBox<String> papelBox = new JComboBox<>(new String[]{"COORDENADOR", "DOCENTE", "EGRESSO"});

        JPanel panel = new JPanel(new GridLayout(0, 2, 4, 4));
        panel.add(new JLabel("Nome:"));
        panel.add(nome);
        panel.add(new JLabel("E-mail:"));
        panel.add(email);
        panel.add(new JLabel("Papel:"));
        panel.add(papelBox);

        int r = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Novo Usuário",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        if (r != JOptionPane.OK_OPTION) return;

        String n = nome.getText().trim();
        String e = email.getText().trim();
        String p = String.valueOf(papelBox.getSelectedItem());
        if (n.isEmpty() || e.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e e-mail são obrigatórios.");
            return;
        }
        com.egressos.service.UsersService usersService = new com.egressos.service.UsersService();
        com.egressos.service.UsersService.ResultadoCriacao res =
                usersService.criarUsuario(n, e, com.egressos.model.Papel.valueOf(p));
        if (res.isEmailJaExiste()) {
            JOptionPane.showMessageDialog(this, "Já existe um usuário com esse e-mail.");
            return;
        }
        JOptionPane.showMessageDialog(this,
                "Usuário criado com sucesso.\nSenha temporária: " + res.getSenhaTemporaria());
        if (onCreated != null) onCreated.run();
    }

    private JPanel buildUsersTab() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        JPanel filtros = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        JTextField nomeF = new JTextField();
        JTextField emailF = new JTextField();
        JComboBox<String> papelF = new JComboBox<>(new String[]{"", "COORDENADOR", "DOCENTE", "EGRESSO"});
        int y = 0;
        c.gridx = 0;
        c.gridy = y;
        filtros.add(new JLabel("Nome contém:"), c);
        c.gridx = 1;
        filtros.add(nomeF, c);
        c.gridx = 2;
        filtros.add(new JLabel("E-mail contém:"), c);
        c.gridx = 3;
        filtros.add(emailF, c);
        y++;
        c.gridx = 0;
        c.gridy = y;
        filtros.add(new JLabel("Papel:"), c);
        c.gridx = 1;
        filtros.add(papelF, c);
        JButton buscar = new JButton("Atualizar");
        c.gridx = 3;
        filtros.add(buscar, c);
        root.add(filtros, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Nome", "E-mail", "Papel", "Precisa trocar senha", "Criado em"}, 0) {
            @Override
            public boolean isCellEditable(int r, int col) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Recarregar");
        JButton novoUsuarioBtn = new JButton("Novo Usuário");
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(novoUsuarioBtn);
        south.add(refreshBtn);
        root.add(south, BorderLayout.SOUTH);

        Runnable loader = () -> {
            List<Usuario> lista = usuariosDao.listar();
            String nf = nomeF.getText().trim().toLowerCase();
            String ef = emailF.getText().trim().toLowerCase();
            String pf = String.valueOf(papelF.getSelectedItem());
            List<Usuario> filtrado = lista.stream().filter(u -> {
                if (!nf.isEmpty() && (u.getNome() == null || !u.getNome().toLowerCase().contains(nf)))
                    return false;
                if (!ef.isEmpty() && (u.getEmail() == null || !u.getEmail().toLowerCase().contains(ef)))
                    return false;
                if (pf != null && !pf.isBlank() && !u.getPapel().name().equalsIgnoreCase(pf))
                    return false;
                return true;
            }).collect(java.util.stream.Collectors.toList());
            model.setRowCount(0);
            for (Usuario u : filtrado) {
                model.addRow(new Object[]{
                        u.getId(),
                        u.getNome(),
                        u.getEmail(),
                        u.getPapel().name(),
                        u.isPrecisaTrocarSenha() ? "Sim" : "Não",
                        u.getCriadoEm().toString()
                });
            }
        };
        buscar.addActionListener(e -> loader.run());
        refreshBtn.addActionListener(e -> loader.run());
        loader.run();

        novoUsuarioBtn.addActionListener(e -> abrirDialogNovoUsuario(loader));

        return root;
    }

}
