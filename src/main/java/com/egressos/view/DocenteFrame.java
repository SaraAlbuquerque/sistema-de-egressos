package com.egressos.view;

import com.egressos.dao.EgressosDao;
import com.egressos.dao.EventosDao;
import com.egressos.dao.UsuariosDao;
import com.egressos.dao.VisualizacoesDocenteDao;
import com.egressos.model.EgressoProfile;
import com.egressos.model.EventoChave;
import com.egressos.model.Papel;
import com.egressos.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class DocenteFrame extends JFrame {

    private final UsuariosDao usuariosDao = new UsuariosDao();
    private final EgressosDao egressosDao = new EgressosDao();
    private final EventosDao eventosDao = new EventosDao();
    private final VisualizacoesDocenteDao visDao = new VisualizacoesDocenteDao();

    private final Usuario docenteRef;

    private final JTextField nomeField   = new JTextField();
    private final JTextField emailField  = new JTextField();
    private final JTextField cursoField  = new JTextField();
    private final JTextField anoDeField  = new JTextField();
    private final JTextField anoAteField = new JTextField();
    private final JTextField areaField   = new JTextField();
    private final JTextField cidadeField = new JTextField();
    private final JTextField estadoField = new JTextField();
    private final JTextField paisField   = new JTextField();
    private final JCheckBox  somenteEmpregados = new JCheckBox("Somente empregados");
    private final JButton    buscarBtn   = new JButton("Buscar");

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID","Nome","Curso","Ano","Eventos"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    private final DefaultTableModel recentesModel = new DefaultTableModel(
            new Object[]{"ID","Quando","Egresso"},0){
        @Override public boolean isCellEditable(int r,int c){return false;}
    };
    private final JTable recentesTable = new JTable(recentesModel);

    private final DateTimeFormatter DF_EVENTO = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter DF_VIS    = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    public DocenteFrame(Usuario docente) {
        super("Egressos — Consulta (Docente)");
        this.docenteRef = docente;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== topo com título + botões =====
        JPanel topo = new JPanel(new BorderLayout());
        topo.add(new JLabel("   Consulta de Egressos — Docente"), BorderLayout.WEST);

        JButton trocarSenhaBtn = new JButton("Trocar senha");
        trocarSenhaBtn.addActionListener(e -> {
            ChangePasswordDialog dlg = new ChangePasswordDialog(this, docenteRef);
            dlg.setLocationRelativeTo(this);
            dlg.setVisible(true);
        });

        JButton sairBtn = new JButton("Sair");
        sairBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        JPanel topoEast = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topoEast.add(trocarSenhaBtn);
        topoEast.add(sairBtn);
        topo.add(topoEast, BorderLayout.EAST);

        // ===== filtros =====
        JPanel filtros = buildFiltros();

        JPanel header = new JPanel(new BorderLayout());
        header.add(topo, BorderLayout.NORTH);
        header.add(filtros, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // ===== tabela principal =====
        table.setAutoCreateRowSorter(true);
        // esconder coluna ID
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (table.getSelectedRow() >= 0) {
                    abrirPerfilDaTabelaPrincipal();
                }
            }
        });

        JPanel recentesPanel = buildRecentesPanel();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.8);
        split.setLeftComponent(new JScrollPane(table));
        split.setRightComponent(recentesPanel);
        add(split, BorderLayout.CENTER);

        // clique nos recentes abre perfil
        recientesTableAddListener();

        buscarBtn.addActionListener(e -> aplicarBusca());

        aplicarBusca();
        carregarRecentes();
    }

    private void recientesTableAddListener() {
        recentesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (recentesTable.getSelectedRow() >= 0) {
                    abrirPerfilDosRecentes();
                }
            }
        });
    }

    private JPanel buildFiltros() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        aplicarMascaraAnoQuatroDigitos(anoDeField);
        aplicarMascaraAnoQuatroDigitos(anoAteField);

        int y = 0;
        c.gridx=0; c.gridy=y; p.add(new JLabel("Nome contém:"), c);
        c.gridx=1; p.add(nomeField, c);
        c.gridx=2; p.add(new JLabel("E-mail contém:"), c);
        c.gridx=3; p.add(emailField, c);

        y++;
        c.gridx=0; c.gridy=y; p.add(new JLabel("Curso:"), c);
        c.gridx=1; p.add(cursoField, c);
        c.gridx=2; p.add(new JLabel("Área:"), c);
        c.gridx=3; p.add(areaField, c);

        y++;
        c.gridx=0; c.gridy=y; p.add(new JLabel("Ano de:"), c);
        c.gridx=1; p.add(anoDeField, c);
        c.gridx=2; c.gridy=y; p.add(new JLabel("Ano até:"), c);
        c.gridx=3; p.add(anoAteField, c);

        y++;
        c.gridx=0; c.gridy=y; p.add(new JLabel("Cidade:"), c);
        c.gridx=1; p.add(cidadeField, c);
        c.gridx=2; p.add(new JLabel("Estado:"), c);
        c.gridx=3; p.add(estadoField, c);

        y++;
        c.gridx=0; c.gridy=y; p.add(new JLabel("País:"), c);
        c.gridx=1; p.add(paisField, c);
        c.gridx=2; p.add(somenteEmpregados, c);

        JButton limparBtn = new JButton("Limpar filtros");
        limparBtn.addActionListener(e -> {
            nomeField.setText("");
            emailField.setText("");
            cursoField.setText("");
            anoDeField.setText("");
            anoAteField.setText("");
            areaField.setText("");
            cidadeField.setText("");
            estadoField.setText("");
            paisField.setText("");
            somenteEmpregados.setSelected(false);
            aplicarBusca();
        });

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        botoes.add(limparBtn);
        botoes.add(buscarBtn);

        c.gridx=3; c.gridy=y; p.add(botoes, c);

        return p;
    }

    private JPanel buildRecentesPanel(){
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel("Últimos egressos visualizados"), BorderLayout.NORTH);

        recentesTable.getColumnModel().getColumn(0).setMinWidth(0);
        recentesTable.getColumnModel().getColumn(0).setMaxWidth(0);
        recentesTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        p.add(new JScrollPane(recentesTable), BorderLayout.CENTER);
        return p;
    }

    private void carregarRecentes(){
        recentesModel.setRowCount(0);
        var ult = visDao.ultimos(docenteRef.getId(), 5);
        Map<String, Usuario> porId = usuariosDao.listar().stream()
                .collect(Collectors.toMap(Usuario::getId, u->u));
        for (var r : ult){
            Usuario u = porId.get(r.egressoUsuarioId);
            String nome = (u == null ? r.egressoUsuarioId : u.getNome());
            String quando;
            try {
                quando = DF_VIS.format(r.ts.atZone(ZoneId.systemDefault()));
            } catch (Exception ex) {
                quando = r.ts.toString();
            }
            recentesModel.addRow(new Object[]{
                    r.egressoUsuarioId,
                    quando,
                    nome
            });
        }
    }

    private void aplicarBusca() {
        try {
            List<Usuario> usuarios = usuariosDao.listar().stream()
                    .filter(u -> u.getPapel() == Papel.EGRESSO)
                    .collect(Collectors.toList());

            Map<String, Usuario> porId = usuarios.stream()
                    .collect(Collectors.toMap(Usuario::getId, u -> u));

            List<EgressoProfile> perfis = egressosDao.listarTodos();

            List<EgressoProfile> filtrado = perfis.stream().filter(p -> {
                Usuario u = porId.get(p.getUsuarioId());
                if (u == null) return false;

                String t;

                t = nomeField.getText().trim().toLowerCase();
                if (!t.isEmpty() && (u.getNome()==null || !u.getNome().toLowerCase().contains(t))) return false;

                t = emailField.getText().trim().toLowerCase();
                if (!t.isEmpty() && (u.getEmail()==null || !u.getEmail().toLowerCase().contains(t))) return false;

                t = cursoField.getText().trim().toLowerCase();
                if (!t.isEmpty() && (p.getCurso()==null || !p.getCurso().toLowerCase().contains(t))) return false;

                t = areaField.getText().trim().toLowerCase();
                if (!t.isEmpty() && (p.getAreaAtuacao()==null || !p.getAreaAtuacao().toLowerCase().contains(t))) return false;

                t = cidadeField.getText().trim().toLowerCase();
                if (!t.isEmpty() && (p.getCidade()==null || !p.getCidade().toLowerCase().contains(t))) return false;

                t = estadoField.getText().trim().toLowerCase();
                if (!t.isEmpty() && (p.getEstado()==null || !p.getEstado().toLowerCase().contains(t))) return false;

                t = paisField.getText().trim().toLowerCase();
                if (!t.isEmpty() && (p.getPais()==null || !p.getPais().toLowerCase().contains(t))) return false;

                if (somenteEmpregados.isSelected() && !p.isEmpregado()) return false;

                int ano = p.getAnoFormacao();
                if (!anoDeField.getText().trim().isEmpty()) {
                    try {
                        int aDe = Integer.parseInt(anoDeField.getText().trim());
                        if (ano < aDe) return false;
                    } catch (Exception ignore) {}
                }
                if (!anoAteField.getText().trim().isEmpty()) {
                    try {
                        int aAte = Integer.parseInt(anoAteField.getText().trim());
                        if (ano > aAte) return false;
                    } catch (Exception ignore) {}
                }
                return true;
            }).collect(Collectors.toList());

            Map<String, List<EventoChave>> eventosPorEgresso = new HashMap<>();
            for (EventoChave ev : eventosDao.listarTodos()) {
                eventosPorEgresso.computeIfAbsent(ev.getEgressoId(), k -> new ArrayList<>()).add(ev);
            }

            model.setRowCount(0);
            for (EgressoProfile p : filtrado) {
                Usuario u = porId.get(p.getUsuarioId());
                if (u == null) continue;

                List<EventoChave> evs = eventosPorEgresso
                        .getOrDefault(p.getUsuarioId(), Collections.emptyList());

                String eventosLista = evs.stream()
                        .map(EventoChave::getTitulo)
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.joining("; "));

                if (eventosLista.isEmpty()) {
                    eventosLista = "(sem eventos cadastrados)";
                }

                model.addRow(new Object[]{
                        p.getUsuarioId(),
                        u.getNome(),
                        p.getCurso(),
                        p.getAnoFormacao(),
                        eventosLista
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro na consulta: " + ex.getMessage());
        }
    }

    private void abrirPerfilDaTabelaPrincipal() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int m = table.convertRowIndexToModel(row);
        String egressoId = String.valueOf(model.getValueAt(m, 0));

        Usuario eg = usuariosDao.buscarPorId(egressoId).orElse(null);
        if (eg == null) {
            JOptionPane.showMessageDialog(this, "Egresso não encontrado.");
            return;
        }
        registrarEExibirPerfil(eg);
    }

    private void abrirPerfilDosRecentes() {
        int row = recentesTable.getSelectedRow();
        if (row < 0) return;
        int m = recentesTable.convertRowIndexToModel(row);
        String egressoId = String.valueOf(recentesModel.getValueAt(m, 0));

        Usuario eg = usuariosDao.buscarPorId(egressoId).orElse(null);
        if (eg == null) {
            JOptionPane.showMessageDialog(this, "Egresso não encontrado.");
            return;
        }
        registrarEExibirPerfil(eg);
    }

    private void registrarEExibirPerfil(Usuario egresso) {
        try {
            visDao.registrar(docenteRef.getId(), egresso.getId());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Falha ao registrar visualização: " + ex.getMessage());
        }
        new PerfilFrame(egresso).setVisible(true);
        carregarRecentes();
    }

    private String n(String s) { return s==null? "" : s; }

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
