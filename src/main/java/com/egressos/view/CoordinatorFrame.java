package com.egressos.view;

import com.egressos.auth.Passwords;
import com.egressos.controller.RelatorioController;
import com.egressos.dao.EventosDao;
import com.egressos.dao.SolicitacoesDao;
import com.egressos.dao.UsuariosDao;
import com.egressos.model.EventoChave;
import com.egressos.model.Papel;
import com.egressos.model.Usuario;
import com.egressos.util.DatasBR;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class CoordinatorFrame extends JFrame {

    private final RelatorioController relatorioController = new RelatorioController();
    private final UsuariosDao usuariosDao = new UsuariosDao();
    private final SolicitacoesDao solicitacoesDao = new SolicitacoesDao();
    private final EventosDao eventosDao = new EventosDao();

    private Usuario usuarioAtual;

    JTextField de = new JTextField();
    JTextField ate = new JTextField();


    private Runnable usuariosReload;

    private CoordinatorEventosPanel eventosPanel;

    public CoordinatorFrame() {
        super("Área do Coordenador");
        setSize(1200,700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Usuários", buildUsersTab());
        tabs.add("Solicitações", buildRequestsTab());
        tabs.add("Relatórios", buildReportsTab());


        eventosPanel = new CoordinatorEventosPanel();
        tabs.add("Eventos", eventosPanel);

        JButton alterarSenha = new JButton("Alterar senha");
        alterarSenha.addActionListener(e -> abrirAlterarSenha());

        JButton sair = new JButton("Sair");
        sair.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        JPanel botoesTopo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoesTopo.add(alterarSenha);
        botoesTopo.add(sair);

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(new JLabel("   Coordenador"), BorderLayout.WEST);
        topo.add(botoesTopo, BorderLayout.EAST);

        add(topo, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    public CoordinatorFrame(Usuario u) {
        this();
        this.usuarioAtual = u;
    }

    private void abrirAlterarSenha() {
        if (usuarioAtual == null) {
            JOptionPane.showMessageDialog(this, "Usuário atual não definido.");
            return;
        }
        ChangePasswordDialog dlg = new ChangePasswordDialog(this, usuarioAtual);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }


    private JPanel buildRequestsTab() {

        JPanel root = new JPanel(new BorderLayout(8,8));

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID","Nome","E-mail","Papel","Criado em"},0){

            public boolean isCellEditable(int r,int c){ return false;}
        };

        JTable tabela = new JTable(model);
        tabela.setAutoCreateRowSorter(true);


        tabela.getColumnModel().getColumn(0).setMinWidth(0);
        tabela.getColumnModel().getColumn(0).setMaxWidth(0);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(0);

        root.add(new JScrollPane(tabela),BorderLayout.CENTER);

        Runnable loadSolicitacoes = () -> {
            model.setRowCount(0);
            solicitacoesDao.listar().forEach(r -> model.addRow(new Object[]{
                    r[0], r[1], r[2], r[3], DatasBR.formatInstant(Instant.parse(r[4]))
            }));
        };
        loadSolicitacoes.run();

        JButton aprovar = new JButton("Aprovar");
        JButton remover = new JButton("Remover");

        aprovar.addActionListener(e -> {
            int row = tabela.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Selecione uma solicitação.");
                return;
            }

            row = tabela.convertRowIndexToModel(row);

            String id    = model.getValueAt(row,0).toString();
            String nome  = model.getValueAt(row,1).toString();
            String email = model.getValueAt(row,2).toString();
            Papel papel  = Papel.valueOf(model.getValueAt(row,3).toString().toUpperCase());

            String senhaTemp = UUID.randomUUID().toString().replace("-","").substring(0,10);
            String hash = Passwords.sha256(senhaTemp);

            Usuario novo = new Usuario(
                    UUID.randomUUID().toString(),
                    email, nome, papel, hash, true, Instant.now()
            );

            usuariosDao.salvarOuAtualizar(novo);
            solicitacoesDao.remover(id);

            loadSolicitacoes.run();
            if (usuariosReload != null) {
                usuariosReload.run();
            }

            JOptionPane.showMessageDialog(this,
                    "Usuário criado!\nSenha temporária copiada: " + senhaTemp);

            Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new StringSelection(senhaTemp), null);
        });

        remover.addActionListener(e -> {
            int row = tabela.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Selecione uma solicitação.");
                return;
            }
            row = tabela.convertRowIndexToModel(row);
            String id = model.getValueAt(row, 0).toString();
            solicitacoesDao.remover(id);
            loadSolicitacoes.run();
        });

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(remover); south.add(aprovar);
        root.add(south,BorderLayout.SOUTH);
        return root;
    }

    private JPanel buildUsersTab() {

        JPanel root = new JPanel(new BorderLayout(8,8));

        JTextField nomeF = new JTextField();
        JTextField emailF = new JTextField();
        JComboBox<String> papelF =
                new JComboBox<>(new String[]{"","COORDENADOR","DOCENTE","EGRESSO"});

        JPanel filtros = new JPanel(new GridLayout(1,6,6,6));
        filtros.add(new JLabel("Nome contém:"));
        filtros.add(nomeF);
        filtros.add(new JLabel("Email contém:"));
        filtros.add(emailF);
        filtros.add(new JLabel("Papel:"));
        filtros.add(papelF);

        JButton limpar = new JButton("Limpar filtros");
        JButton ref = new JButton("Atualizar");

        JPanel botoesFiltro = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoesFiltro.add(limpar);
        botoesFiltro.add(ref);

        JPanel norte = new JPanel(new BorderLayout(4,4));
        norte.add(filtros, BorderLayout.CENTER);
        norte.add(botoesFiltro, BorderLayout.SOUTH);

        root.add(norte,BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID","Nome","E-mail","Papel","Trocar Senha","Criado"},0){
            public boolean isCellEditable(int r,int c){ return false;}
        };

        JTable tabela = new JTable(model);
        tabela.setAutoCreateRowSorter(true);

        tabela.getColumnModel().getColumn(0).setMinWidth(0);
        tabela.getColumnModel().getColumn(0).setMaxWidth(0);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(0);

        root.add(new JScrollPane(tabela),BorderLayout.CENTER);

        Runnable loadUsuarios = () -> {
            model.setRowCount(0);
            usuariosDao.listar().stream().filter(u -> {

                if (!nomeF.getText().isBlank() &&
                        (u.getNome() == null ||
                                !u.getNome().toLowerCase().contains(nomeF.getText().toLowerCase())))
                    return false;

                if (!emailF.getText().isBlank() &&
                        (u.getEmail() == null ||
                                !u.getEmail().toLowerCase().contains(emailF.getText().toLowerCase())))
                    return false;

                if (papelF.getSelectedItem() != null &&
                        !papelF.getSelectedItem().toString().isBlank() &&
                        !u.getPapel().name().equals(papelF.getSelectedItem().toString()))
                    return false;

                return true;
            }).forEach(u -> model.addRow(new Object[]{
                    u.getId(),u.getNome(),u.getEmail(),u.getPapel().name(),
                    u.isPrecisaTrocarSenha()?"Sim":"Não",
                    DatasBR.formatInstant(u.getCriadoEm())
            }));
        };

        this.usuariosReload = loadUsuarios;
        loadUsuarios.run();

        limpar.addActionListener(e -> {
            nomeF.setText("");
            emailF.setText("");
            papelF.setSelectedIndex(0);
            loadUsuarios.run();
        });
        ref.addActionListener(e -> loadUsuarios.run());

        JButton novo = new JButton("Novo Usuário");
        novo.addActionListener(e -> abrirDialogNovoUsuario(loadUsuarios));

        JButton excluir = new JButton("Excluir Usuário");
        excluir.addActionListener(e -> excluirUsuarioSelecionado(tabela, model, loadUsuarios));

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(novo);
        south.add(excluir);
        root.add(south,BorderLayout.SOUTH);

        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    int row = tabela.rowAtPoint(e.getPoint());
                    if (row < 0) return;
                    int m = tabela.convertRowIndexToModel(row);
                    String papel = String.valueOf(model.getValueAt(m, 3));
                    if (!"EGRESSO".equalsIgnoreCase(papel)) return;

                    String id = String.valueOf(model.getValueAt(m, 0));
                    Usuario eg = buscarUsuarioPorId(id);
                    if (eg != null) {
                        new PerfilFrame(eg).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(CoordinatorFrame.this,
                                "Não foi possível carregar o perfil do egresso.");
                    }
                }
            }
        });

        return root;
    }

    private Usuario buscarUsuarioPorId(String id) {
        return usuariosDao.listar().stream()
                .filter(u -> id.equals(u.getId()))
                .findFirst()
                .orElse(null);
    }

    private void excluirUsuarioSelecionado(JTable tabela,
                                           DefaultTableModel model,
                                           Runnable reload) {

        int row = tabela.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para excluir.");
            return;
        }
        int m = tabela.convertRowIndexToModel(row);
        String id = String.valueOf(model.getValueAt(m, 0));

        if (usuarioAtual != null && usuarioAtual.getId().equals(id)) {
            JOptionPane.showMessageDialog(this,
                    "Você não pode excluir a si mesmo.");
            return;
        }

        Usuario alvo = usuariosDao.buscarPorId(id).orElse(null);
        if (alvo == null) {
            JOptionPane.showMessageDialog(this,
                    "Usuário não encontrado.");
            return;
        }

        int op = JOptionPane.showConfirmDialog(this,
                "Confirma a exclusão deste usuário"
                        + (alvo.getPapel() == com.egressos.model.Papel.EGRESSO ? " e de todos os seus eventos?" : "?"),
                "Excluir usuário",
                JOptionPane.YES_NO_OPTION);

        if (op != JOptionPane.YES_OPTION) return;

        try {
            // se for egresso, remove também seus eventos-chave
            if (alvo.getPapel() == com.egressos.model.Papel.EGRESSO) {
                List<EventoChave> todos = eventosDao.listarTodos();
                boolean mudou = todos.removeIf(ev -> id.equals(ev.getEgressoId()));
                if (mudou) {
                    eventosDao.salvarTodos(todos);
                }
            }

            usuariosDao.remover(id);
            reload.run();


            if (eventosPanel != null) {
                eventosPanel.recarregar();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao excluir usuário: " + ex.getMessage());
        }
    }


    private void abrirDialogNovoUsuario(Runnable reload) {

        JTextField nome=new JTextField(), email=new JTextField();
        JComboBox<String> papel=
                new JComboBox<>(new String[]{"COORDENADOR","DOCENTE","EGRESSO"});

        JPanel p=new JPanel(new GridLayout(0,2,6,6));
        p.add(new JLabel("Nome:")); p.add(nome);
        p.add(new JLabel("Email:")); p.add(email);
        p.add(new JLabel("Papel:")); p.add(papel);

        if(JOptionPane.showConfirmDialog(this,p,"Criar Usuário",
                JOptionPane.OK_CANCEL_OPTION)!=JOptionPane.OK_OPTION) return;

        String senhaTmp = UUID.randomUUID().toString().replace("-","").substring(0,10);
        String hash = Passwords.sha256(senhaTmp);

        Usuario u=new Usuario(UUID.randomUUID().toString(),
                email.getText(),nome.getText(),Papel.valueOf(papel.getSelectedItem().toString()),
                hash,true,Instant.now());

        usuariosDao.salvarOuAtualizar(u);

        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(senhaTmp),null);

        JOptionPane.showMessageDialog(this,
                "Usuário criado com sucesso.\nSenha temporária copiada:\n"+senhaTmp);

        reload.run();
    }


    private JPanel buildReportsTab(){

        JPanel panel=new JPanel(new GridLayout(0,1,8,8));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        aplicarMascaraDataBR(de);
        aplicarMascaraDataBR(ate);

        JButton tipoCsv=new JButton("Exportar Eventos por Tipo (CSV)");
        JButton orgCsv=new JButton("Exportar por Organização (CSV)");
        JButton periodo=new JButton("Exportar por Período (CSV)");
        JButton pdf=new JButton("Exportar PDF Básico");

        panel.add(pdf);
        panel.add(tipoCsv);
        panel.add(orgCsv);
        panel.add(new JLabel("Período (dd/MM/yyyy):"));
        panel.add(new JLabel("De:"));
        panel.add(de);
        panel.add(new JLabel("Até:"));
        panel.add(ate);
        panel.add(periodo);

        tipoCsv.addActionListener(e -> exportarCsv("tipo.csv",
                RelatorioController.TipoRelatorio.EVENTOS_POR_TIPO,null,null,null));

        orgCsv.addActionListener(e -> exportarCsv("org.csv",
                RelatorioController.TipoRelatorio.EVENTOS_POR_ORGANIZACAO,null,null,null));

        periodo.addActionListener(e -> exportarCsv("periodo.csv",
                RelatorioController.TipoRelatorio.EVENTOS_POR_PERIODO,
                DatasBR.parse(de.getText()),DatasBR.parse(ate.getText()),null));

        pdf.addActionListener(e -> exportarPdf("eventos.pdf",
                RelatorioController.TipoRelatorio.EVENTOS_BASICO));

        return panel;
    }


    private void exportarCsv(String nome,RelatorioController.TipoRelatorio tipo,
                             LocalDate i, LocalDate f, String loc){

        var arq=escolherArquivo(nome); if(arq==null) return;

        try{
            relatorioController.gerarCsv(tipo,arq.toPath(),i,f,loc);
            JOptionPane.showMessageDialog(this,"Gerado com sucesso!");
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this,"Erro: "+ex.getMessage());
        }
    }

    private void exportarPdf(String nome,RelatorioController.TipoRelatorio tipo){

        var arq=escolherArquivo(nome); if(arq==null) return;
        try{
            relatorioController.gerarPdf(tipo,arq.toPath());
            JOptionPane.showMessageDialog(this,"PDF gerado.");
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this,"Erro: "+ex.getMessage());
        }
    }


    private void aplicarMascaraDataBR(JTextField campo){
        AbstractDocument doc = (AbstractDocument) campo.getDocument();

        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {

                String valorAtual = fb.getDocument().getText(0, fb.getDocument().getLength());
                String novoValor  = valorAtual.substring(0, offset) + text + valorAtual.substring(offset + length);

                novoValor = novoValor.replaceAll("[^0-9]", ""); // só números

                if(novoValor.length() > 8) return; // ddMMyyyy (limite)

                StringBuilder out = new StringBuilder();

                for(int i = 0; i < novoValor.length(); i++){
                    out.append(novoValor.charAt(i));
                    if(i == 1 || i == 3) out.append('/'); // dd/MM/yyyy
                }

                fb.remove(0, fb.getDocument().getLength());
                fb.insertString(0, out.toString(), attrs);
            }
        });
    }

    private java.io.File escolherArquivo(String s){
        var fc=new JFileChooser();
        fc.setSelectedFile(new java.io.File(s));
        return fc.showSaveDialog(this)==JFileChooser.APPROVE_OPTION?
                fc.getSelectedFile():null;
    }
}
