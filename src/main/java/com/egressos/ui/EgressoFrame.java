package com.egressos.ui;

import com.egressos.dao.EgressosDao;
import com.egressos.dao.EventosDao;
import com.egressos.model.EgressoProfile;
import com.egressos.model.EventoChave;
import com.egressos.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EgressoFrame extends JFrame {
    private final EgressosDao egressosDao = new EgressosDao();
    private final EventosDao eventosDao = new EventosDao();
    private final Usuario egresso;

    // componentes do perfil (existentes + novos)
    private final JTextField cursoField = new JTextField();
    private final JTextField anoField = new JTextField();
    private final JTextField areaField = new JTextField();
    private final JTextField escolaridadeField = new JTextField();
    private final JCheckBox empregadoCB = new JCheckBox("Empregado atualmente");
    private final JTextField empresaField = new JTextField();
    private final JTextField cidadeField = new JTextField();
    private final JTextField estadoField = new JTextField();
    private final JTextField paisField = new JTextField();

    // LGPD
    private final JCheckBox cbVisContato = new JCheckBox("Permitir exibir meu contato a docentes");
    private final JCheckBox cbVisEmpresa = new JCheckBox("Permitir exibir minha empresa a docentes");

    // Recentes
    private final DefaultTableModel recentesModel = new DefaultTableModel(
            new Object[]{"Data","Tipo","Título"},0){ @Override public boolean isCellEditable(int r,int c){ return false; }};

    public EgressoFrame(Usuario egresso){
        super("Área do Egresso");
        this.egresso = egresso;
        setSize(1000,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Perfil", buildPerfilTab());
        tabs.add("Recentes", buildRecentesTab());
        tabs.add("Eventos", new EventosDialog(this, egresso).getContentPane());
        setContentPane(tabs);

        carregarPerfil();
        carregarRecentes();
    }

    private JPanel buildPerfilTab(){
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        int y = 0;

        c.gridx=0; c.gridy=y; p.add(new JLabel("Curso:"), c);
        c.gridx=1; p.add(cursoField, c);
        y++;
        c.gridx=0; c.gridy=y; p.add(new JLabel("Ano de formação:"), c);
        c.gridx=1; p.add(anoField, c);
        y++;
        c.gridx=0; c.gridy=y; p.add(new JLabel("Área de atuação:"), c);
        c.gridx=1; p.add(areaField, c);
        y++;
        c.gridx=0; c.gridy=y; p.add(new JLabel("Escolaridade:"), c);
        c.gridx=1; p.add(escolaridadeField, c);
        y++;
        c.gridx=0; c.gridy=y; p.add(empregadoCB, c);
        y++;
        c.gridx=0; c.gridy=y; p.add(new JLabel("Empresa atual:"), c);
        c.gridx=1; p.add(empresaField, c);
        y++;
        c.gridx=0; c.gridy=y; p.add(new JLabel("Cidade:"), c);
        c.gridx=1; p.add(cidadeField, c);
        y++;
        c.gridx=0; c.gridy=y; p.add(new JLabel("Estado:"), c);
        c.gridx=1; p.add(estadoField, c);
        y++;
        c.gridx=0; c.gridy=y; p.add(new JLabel("País:"), c);
        c.gridx=1; p.add(paisField, c);
        y++;
        c.gridx=0; c.gridy=y; p.add(cbVisContato, c);
        y++;
        c.gridx=0; c.gridy=y; p.add(cbVisEmpresa, c);
        y++;

        JButton salvar = new JButton("Salvar Perfil");
        salvar.addActionListener(e -> salvarPerfil());
        c.gridx=1; c.gridy=y; c.anchor = GridBagConstraints.EAST;
        p.add(salvar, c);
        return p;
    }

    private JPanel buildRecentesTab(){
        JPanel p = new JPanel(new BorderLayout());
        JTable t = new JTable(recentesModel);
        t.setAutoCreateRowSorter(true);
        p.add(new JScrollPane(t), BorderLayout.CENTER);
        return p;
    }

    private void carregarPerfil(){
        List<EgressoProfile> todos = egressosDao.listarTodos();
        EgressoProfile perfil = todos.stream()
                .filter(pp -> egresso.getId().equals(pp.getUsuarioId()))
                .findFirst().orElseGet(() -> {
                    EgressoProfile np = new EgressoProfile();
                    np.setUsuarioId(egresso.getId());
                    todos.add(np);
                    egressosDao.salvarTodos(todos);
                    return np;
                });

        cursoField.setText(nullToEmpty(perfil.getCurso()));
        anoField.setText(String.valueOf(perfil.getAnoFormacao()));
        areaField.setText(nullToEmpty(perfil.getAreaAtuacao()));
        escolaridadeField.setText(nullToEmpty(perfil.getEscolaridadeAtual()));
        empregadoCB.setSelected(perfil.isEmpregado());
        empresaField.setText(nullToEmpty(perfil.getEmpresaAtual()));
        cidadeField.setText(nullToEmpty(perfil.getCidade()));
        estadoField.setText(nullToEmpty(perfil.getEstado()));
        paisField.setText(nullToEmpty(perfil.getPais()));
        cbVisContato.setSelected(perfil.isPermitirExibirContato());
        cbVisEmpresa.setSelected(perfil.isPermitirExibirEmpresa());
    }

    private void salvarPerfil(){
        List<EgressoProfile> todos = egressosDao.listarTodos();
        EgressoProfile perfil = todos.stream()
                .filter(pp -> egresso.getId().equals(pp.getUsuarioId()))
                .findFirst().orElseGet(() -> {
                    EgressoProfile np = new EgressoProfile();
                    np.setUsuarioId(egresso.getId());
                    todos.add(np);
                    return np;
                });

        perfil.setCurso(cursoField.getText().trim());
        try { perfil.setAnoFormacao(Integer.parseInt(anoField.getText().trim())); } catch (Exception ignore){}
        perfil.setAreaAtuacao(areaField.getText().trim());
        perfil.setEscolaridadeAtual(escolaridadeField.getText().trim());
        perfil.setEmpregado(empregadoCB.isSelected());
        perfil.setEmpresaAtual(empresaField.getText().trim());
        perfil.setCidade(cidadeField.getText().trim());
        perfil.setEstado(estadoField.getText().trim());
        perfil.setPais(paisField.getText().trim());
        perfil.setPermitirExibirContato(cbVisContato.isSelected());
        perfil.setPermitirExibirEmpresa(cbVisEmpresa.isSelected());

        egressosDao.salvarTodos(todos);
        JOptionPane.showMessageDialog(this, "Perfil salvo.");
    }

    private void carregarRecentes(){
        List<EventoChave> meus = eventosDao.listarTodos().stream()
                .filter(e -> egresso.getId().equals(e.getEgressoId()))
                .sorted(Comparator.comparing(EventoChave::getData, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .limit(5)
                .collect(Collectors.toList());
        recentesModel.setRowCount(0);
        for (EventoChave e : meus){
            recentesModel.addRow(new Object[]{
                    e.getData()==null? "" : e.getData().toString(),
                    e.getTipo()==null? "" : e.getTipo().name(),
                    e.getTitulo()
            });
        }
    }

    private static String nullToEmpty(String s){ return s==null? "" : s; }
}
