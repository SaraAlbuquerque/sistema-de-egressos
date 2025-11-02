package com.egressos.ui;

import com.egressos.dao.EgressosDao;
import com.egressos.dao.EventosDao;
import com.egressos.dao.UsuariosDao;
import com.egressos.dao.VisualizacoesDocenteDao;
import com.egressos.model.EgressoProfile;
import com.egressos.model.EventoChave;
import com.egressos.model.Usuario;
import com.egressos.model.Papel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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

    // filtros
    private final JTextField nomeField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField cursoField = new JTextField();
    private final JTextField anoDeField = new JTextField();
    private final JTextField anoAteField = new JTextField();
    private final JTextField areaField = new JTextField();
    private final JTextField cidadeField = new JTextField();
    private final JTextField estadoField = new JTextField();
    private final JTextField paisField = new JTextField();
    private final JCheckBox somenteEmpregados = new JCheckBox("Somente empregados");
    private final JButton buscarBtn = new JButton("Buscar");

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Nome","E-mail","Curso","Ano","Área","Escolaridade","Empregado","Empresa","Cidade/UF/País","Eventos"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final DefaultTableModel recentesModel = new DefaultTableModel(
            new Object[]{"Quando","Egresso"},0){ @Override public boolean isCellEditable(int r,int c){return false;}};

    private final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public DocenteFrame(Usuario docente) {
        super("Egressos — Consulta (Docente)");
        this.docenteRef = docente;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);

        setJMenuBar(buildMenu());

        JPanel filtros = buildFiltros();
        add(filtros, BorderLayout.NORTH);

        table.setAutoCreateRowSorter(true);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.8);
        split.setLeftComponent(new JScrollPane(table));
        split.setRightComponent(buildRecentesPanel());
        add(split, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton verBtn = new JButton("Registrar visualização do selecionado");
        verBtn.addActionListener(e -> registrarVisualizacaoSelecionada());
        south.add(verBtn);
        add(south, BorderLayout.SOUTH);

        buscarBtn.addActionListener(e -> aplicarBusca());
        aplicarBusca();
        carregarRecentes();
    }

    private JMenuBar buildMenu() {
        JMenuBar bar = new JMenuBar();
        JMenu arquivo = new JMenu("Arquivo");
        JMenuItem trocar = new JMenuItem("Trocar de usuário");
        trocar.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        JMenuItem sair = new JMenuItem("Sair");
        sair.addActionListener(e -> System.exit(0));
        arquivo.add(trocar); arquivo.addSeparator(); arquivo.add(sair);
        bar.add(arquivo);
        return bar;
    }

    private JPanel buildFiltros() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

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
        c.gridx=2; p.add(new JLabel("Ano até:"), c);
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
        c.gridx=3; p.add(buscarBtn, c);

        return p;
    }

    private JPanel buildRecentesPanel(){
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel("Últimos egressos visualizados"), BorderLayout.NORTH);
        JTable t = new JTable(recentesModel);
        p.add(new JScrollPane(t), BorderLayout.CENTER);
        return p;
    }

    private void registrarVisualizacaoSelecionada(){
        int row = table.getSelectedRow();
        if (row < 0){ JOptionPane.showMessageDialog(this, "Selecione uma linha."); return; }
        int m = table.convertRowIndexToModel(row);
        String email = String.valueOf(model.getValueAt(m, 1));
        Usuario eg = usuariosDao.listar().stream()
                .filter(u -> email.equalsIgnoreCase(u.getEmail()))
                .findFirst().orElse(null);
        if (eg == null){ JOptionPane.showMessageDialog(this, "Egresso não encontrado."); return; }
        visDao.registrar(docenteRef.getId(), eg.getId());
        carregarRecentes();
    }

    private void carregarRecentes(){
        recentesModel.setRowCount(0);
        var ult = visDao.ultimos(docenteRef.getId(), 5);
        Map<String, Usuario> porId = usuariosDao.listar().stream()
                .collect(Collectors.toMap(Usuario::getId, u->u));
        for (var r : ult){
            Usuario u = porId.get(r.egressoUsuarioId);
            recentesModel.addRow(new Object[]{ r.ts.toString(), u==null? r.egressoUsuarioId : u.getNome() });
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
                    try { if (ano < Integer.parseInt(anoDeField.getText().trim())) return false; } catch (Exception ignore) {}
                }
                if (!anoAteField.getText().trim().isEmpty()) {
                    try { if (ano > Integer.parseInt(anoAteField.getText().trim())) return false; } catch (Exception ignore) {}
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
                // LGPD: empresa/contato só se permitido
                String empresa = p.isPermitirExibirEmpresa()? p.getEmpresaAtual() : "";
                String email = p.isPermitirExibirContato()? u.getEmail() : "(oculto)";
                List<EventoChave> evs = eventosPorEgresso.getOrDefault(p.getUsuarioId(), Collections.emptyList());
                String eventosResumo;
                if (evs.isEmpty()) {
                    eventosResumo = "0";
                } else {
                    EventoChave ultimo = evs.stream().max(Comparator.comparing(EventoChave::getData,
                            Comparator.nullsLast(Comparator.naturalOrder()))).orElse(null);
                    eventosResumo = evs.size() + " (último: " + (ultimo!=null && ultimo.getData()!=null? ultimo.getData().format(DF) : "-") + ")";
                }
                model.addRow(new Object[]{
                        u.getNome(),
                        email,
                        p.getCurso(),
                        p.getAnoFormacao(),
                        p.getAreaAtuacao(),
                        p.getEscolaridadeAtual(),
                        p.isEmpregado() ? "Sim" : "Não",
                        empresa,
                        String.format("%s/%s/%s", n(p.getCidade()), n(p.getEstado()), n(p.getPais())),
                        eventosResumo
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro na consulta: " + ex.getMessage());
        }
    }

    private String n(String s) { return s==null? "" : s; }
}
