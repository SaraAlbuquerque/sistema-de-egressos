package com.egressos.view;

import com.egressos.dao.EgressosDao;
import com.egressos.dao.EventosDao;
import com.egressos.model.EgressoProfile;
import com.egressos.model.EventoChave;
import com.egressos.model.Usuario;
import com.egressos.util.DatasBR;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EgressoFrame extends JFrame {

    private final EgressosDao egressosDao = new EgressosDao();
    private final EventosDao eventosDao = new EventosDao();
    private final Usuario egresso;

    // Dados do usuário (somente exibição)
    private final JTextField nomeField  = new JTextField();
    private final JTextField emailField = new JTextField();

    // Dados de primeiro acesso / cadastro complementar
    private final JTextField dataNascField = new JTextField(); // dd/MM/yyyy
    private final JTextField anoField      = new JTextField(); // ano de formação/conclusão
    private final JTextField githubField   = new JTextField();
    private final JTextField rede1Field    = new JTextField();
    private final JTextField rede2Field    = new JTextField();

    // Dados de carreira / localização
    private final JTextField cursoField         = new JTextField();
    private final JTextField areaField          = new JTextField();
    private final JTextField escolaridadeField  = new JTextField();
    private final JCheckBox  empregadoCB        = new JCheckBox("Empregado atualmente");
    private final JTextField empresaField       = new JTextField();
    private final JTextField cidadeField        = new JTextField();
    private final JTextField estadoField        = new JTextField();
    private final JTextField paisField          = new JTextField();



    // Eventos do egresso
    private final DefaultTableModel eventosModel = new DefaultTableModel(
            new Object[]{"ID","Data","Tipo","Título","Organização","Local"},0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable eventosTable = new JTable(eventosModel);

    public EgressoFrame(Usuario egresso){
        super("Área do Egresso");
        this.egresso = egresso;
        setSize(1000,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Perfil",  buildPerfilTab());
        tabs.add("Eventos", buildEventosTab());

        // Barra superior com Sair / Alterar senha
        JButton alterarSenhaBtn = new JButton("Alterar senha");
        JButton sairBtn         = new JButton("Sair");

        alterarSenhaBtn.addActionListener(e -> abrirAlterarSenha());
        sairBtn.addActionListener(e -> sairParaLogin());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topBar.add(alterarSenhaBtn);
        topBar.add(sairBtn);

        JPanel root = new JPanel(new BorderLayout());
        root.add(topBar, BorderLayout.NORTH);
        root.add(tabs,  BorderLayout.CENTER);

        setContentPane(root);

        carregarPerfil();
        carregarEventos();
        setPerfilEditavel(false);
    }


    private void sairParaLogin() {
        dispose();
        new LoginFrame().setVisible(true);
    }

    private void abrirAlterarSenha() {
        ChangePasswordDialog dlg = new ChangePasswordDialog(this, egresso);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }


    private JPanel buildPerfilTab(){
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        int y = 0;

        // Nome e e-mail (somente visualização)
        c.gridx=0; c.gridy=y; p.add(new JLabel("Nome:"), c);
        c.gridx=1; p.add(nomeField, c);
        y++;
        c.gridx=0; c.gridy=y; p.add(new JLabel("E-mail:"), c);
        c.gridx=1; p.add(emailField, c);
        y++;

        // Dados principais do primeiro acesso
        c.gridx=0; c.gridy=y; p.add(new JLabel("Data de nascimento (dd/MM/yyyy):"), c);
        c.gridx=1; p.add(dataNascField, c);
        y++;

        c.gridx=0; c.gridy=y; p.add(new JLabel("Ano de formação/conclusão:"), c);
        c.gridx=1; p.add(anoField, c);
        y++;

        c.gridx=0; c.gridy=y; p.add(new JLabel("Usuário do GitHub:"), c);
        c.gridx=1; p.add(githubField, c);
        y++;

        c.gridx=0; c.gridy=y; p.add(new JLabel("Rede social 1:"), c);
        c.gridx=1; p.add(rede1Field, c);
        y++;

        c.gridx=0; c.gridy=y; p.add(new JLabel("Rede social 2:"), c);
        c.gridx=1; p.add(rede2Field, c);
        y++;

        // Demais dados de carreira / localização
        c.gridx=0; c.gridy=y; p.add(new JLabel("Curso:"), c);
        c.gridx=1; p.add(cursoField, c);
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




        JButton editarBtn = new JButton("Editar perfil");
        JButton salvarBtn = new JButton("Salvar perfil");

        editarBtn.addActionListener(e -> setPerfilEditavel(true));
        salvarBtn.addActionListener(e -> salvarPerfil());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(editarBtn);
        botoes.add(salvarBtn);

        c.gridx=0; c.gridy=y; c.gridwidth = 2; c.anchor = GridBagConstraints.EAST;
        p.add(botoes, c);

        nomeField.setEditable(false);
        emailField.setEditable(false);

        return p;
    }

    private void setPerfilEditavel(boolean editavel) {
        dataNascField.setEditable(editavel);
        anoField.setEditable(editavel);
        githubField.setEditable(editavel);
        rede1Field.setEditable(editavel);
        rede2Field.setEditable(editavel);

        cursoField.setEditable(editavel);
        areaField.setEditable(editavel);
        escolaridadeField.setEditable(editavel);
        empregadoCB.setEnabled(editavel);
        empresaField.setEditable(editavel);
        cidadeField.setEditable(editavel);
        estadoField.setEditable(editavel);
        paisField.setEditable(editavel);
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


        nomeField.setText(nullToEmpty(egresso.getNome()));
        emailField.setText(nullToEmpty(egresso.getEmail()));
        dataNascField.setText(DatasBR.format(perfil.getDataNascimento()));
        if (perfil.getAnoFormacao() > 0) {
            anoField.setText(String.valueOf(perfil.getAnoFormacao()));
        } else {
            anoField.setText("");
        }
        githubField.setText(nullToEmpty(perfil.getNickGithub()));
        rede1Field.setText(nullToEmpty(perfil.getRedeSocial1()));
        rede2Field.setText(nullToEmpty(perfil.getRedeSocial2()));
        cursoField.setText(nullToEmpty(perfil.getCurso()));
        areaField.setText(nullToEmpty(perfil.getAreaAtuacao()));
        escolaridadeField.setText(nullToEmpty(perfil.getEscolaridadeAtual()));
        empregadoCB.setSelected(perfil.isEmpregado());
        empresaField.setText(nullToEmpty(perfil.getEmpresaAtual()));
        cidadeField.setText(nullToEmpty(perfil.getCidade()));
        estadoField.setText(nullToEmpty(perfil.getEstado()));
        paisField.setText(nullToEmpty(perfil.getPais()));
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

        String sDataNasc = dataNascField.getText().trim();
        LocalDate dn = DatasBR.parse(sDataNasc);
        if (dn == null) {
            JOptionPane.showMessageDialog(this,
                    "Informe uma data de nascimento válida no formato dd/MM/yyyy.");
            return;
        }

        String sAno = anoField.getText().trim();
        Integer ano = null;
        try {
            if (!sAno.isEmpty()) ano = Integer.parseInt(sAno);
        } catch (Exception ignore) { }
        if (ano == null) {
            JOptionPane.showMessageDialog(this,
                    "Informe o ano de formação/conclusão.");
            return;
        }

        String nick = githubField.getText().trim();
        if (nick.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Informe o usuário do GitHub.");
            return;
        }

        perfil.setDataNascimento(dn);
        perfil.setAnoFormacao(ano);
        perfil.setNickGithub(nick);
        perfil.setRedeSocial1(rede1Field.getText().trim());
        perfil.setRedeSocial2(rede2Field.getText().trim());

        perfil.setCurso(cursoField.getText().trim());
        perfil.setAreaAtuacao(areaField.getText().trim());
        perfil.setEscolaridadeAtual(escolaridadeField.getText().trim());
        perfil.setEmpregado(empregadoCB.isSelected());
        perfil.setEmpresaAtual(empresaField.getText().trim());
        perfil.setCidade(cidadeField.getText().trim());
        perfil.setEstado(estadoField.getText().trim());
        perfil.setPais(paisField.getText().trim());

        egressosDao.salvarTodos(todos);
        setPerfilEditavel(false);
        JOptionPane.showMessageDialog(this, "Perfil salvo com sucesso.");
    }


    private JPanel buildEventosTab() {
        JPanel root = new JPanel(new BorderLayout(8,8));

        eventosTable.setAutoCreateRowSorter(true);
        eventosTable.getColumnModel().getColumn(0).setMinWidth(0);
        eventosTable.getColumnModel().getColumn(0).setMaxWidth(0);
        eventosTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        root.add(new JScrollPane(eventosTable), BorderLayout.CENTER);

        JButton addBtn = new JButton("Adicionar evento");
        JButton delBtn = new JButton("Excluir evento");

        addBtn.addActionListener(e -> adicionarEvento());
        delBtn.addActionListener(e -> excluirEventoSelecionado());

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(addBtn);
        south.add(delBtn);

        root.add(south, BorderLayout.SOUTH);

        return root;
    }

    private void carregarEventos() {
        List<EventoChave> meus = eventosDao.listarTodos().stream()
                .filter(e -> egresso.getId().equals(e.getEgressoId()))
                .sorted(Comparator.comparing(EventoChave::getData,
                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .collect(Collectors.toList());

        eventosModel.setRowCount(0);
        for (EventoChave e : meus) {
            eventosModel.addRow(new Object[]{
                    e.getId(),
                    e.getData()==null? "" : DatasBR.format(e.getData()),
                    e.getTipo()==null? "" : e.getTipo().name(),
                    e.getTitulo(),
                    e.getOrganizacao(),
                    e.getLocal()
            });
        }
    }

    private void adicionarEvento() {
        EventosDialog dlg = new EventosDialog(this, egresso);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
        carregarEventos();
    }

    private void excluirEventoSelecionado() {
        int row = eventosTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um evento para excluir.");
            return;
        }
        int modelRow = eventosTable.convertRowIndexToModel(row);
        String id = String.valueOf(eventosModel.getValueAt(modelRow, 0));

        int op = JOptionPane.showConfirmDialog(
                this,
                "Confirma a exclusão deste evento?",
                "Excluir evento",
                JOptionPane.YES_NO_OPTION
        );
        if (op != JOptionPane.YES_OPTION) return;

        try {
            eventosDao.remover(id);
            carregarEventos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Falha ao remover evento: " + ex.getMessage());
        }
    }

    private static String nullToEmpty(String s){ return s==null? "" : s; }
}
