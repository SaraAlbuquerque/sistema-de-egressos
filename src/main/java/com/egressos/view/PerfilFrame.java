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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PerfilFrame extends JFrame {

    private final Usuario egresso;
    private final EgressosDao egressosDao = new EgressosDao();
    private final EventosDao eventosDao = new EventosDao();

    private final JTextField nomeField  = new JTextField();
    private final JTextField emailField = new JTextField();

    private final JTextField dataNascField = new JTextField();
    private final JTextField anoField      = new JTextField();
    private final JTextField githubField   = new JTextField();
    private final JTextField rede1Field    = new JTextField();
    private final JTextField rede2Field    = new JTextField();

    private final JTextField cursoField         = new JTextField();
    private final JTextField areaField          = new JTextField();
    private final JTextField escolaridadeField  = new JTextField();
    private final JTextField empregadoField     = new JTextField();
    private final JTextField empresaField       = new JTextField();
    private final JTextField cidadeField        = new JTextField();
    private final JTextField estadoField        = new JTextField();
    private final JTextField paisField          = new JTextField();
    private final JTextField visContatoField    = new JTextField();
    private final JTextField visEmpresaField    = new JTextField();

    private final DefaultTableModel eventosModel = new DefaultTableModel(
            new Object[]{"Data","Tipo","Título","Organização","Local"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable eventosTable = new JTable(eventosModel);

    public PerfilFrame(Usuario egresso) {
        super("Perfil do Egresso");
        this.egresso = egresso;
        setSize(900, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Perfil", buildPerfilTab());
        tabs.add("Eventos", buildEventosTab());

        setContentPane(tabs);

        carregarPerfil();
        carregarEventos();
    }

    private JPanel buildPerfilTab() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        int y = 0;

        int labelAlign = SwingConstants.RIGHT;

        c.gridx=0; c.gridy=y; p.add(label("Nome:", labelAlign), c);
        c.gridx=1; p.add(readOnly(nomeField), c); y++;

        c.gridx=0; c.gridy=y; p.add(label("E-mail:", labelAlign), c);
        c.gridx=1; p.add(readOnly(emailField), c); y++;

        c.gridx=0; c.gridy=y; p.add(label("Data de nascimento:", labelAlign), c);
        c.gridx=1; p.add(readOnly(dataNascField), c); y++;

        c.gridx=0; c.gridy=y; p.add(label("Ano de formação/conclusão:", labelAlign), c);
        c.gridx=1; p.add(readOnly(anoField), c); y++;

        c.gridx=0; c.gridy=y; p.add(label("Usuário do GitHub:", labelAlign), c);
        c.gridx=1; p.add(readOnly(githubField), c); y++;

        c.gridx=0; c.gridy=y; p.add(label("Rede social 1:", labelAlign), c);
        c.gridx=1; p.add(readOnly(rede1Field), c); y++;

        c.gridx=0; c.gridy=y; p.add(label("Rede social 2:", labelAlign), c);
        c.gridx=1; p.add(readOnly(rede2Field), c); y++;

        c.gridx=0; c.gridy=y; p.add(label("Curso:", labelAlign), c);
        c.gridx=1; p.add(readOnly(cursoField), c); y++;

        c.gridx=0; c.gridy=y; p.add(label("Área de atuação:", labelAlign), c);
        c.gridx=1; p.add(readOnly(areaField), c); y++;

        c.gridx=0; c.gridy=y; p.add(label("Escolaridade:", labelAlign), c);
        c.gridx=1; p.add(readOnly(escolaridadeField), c); y++;

        c.gridx=0; c.gridy=y; p.add(label("Empregado atualmente:", labelAlign), c);
        c.gridx=1; p.add(readOnly(empregadoField), c); y++;

        c.gridx=0; c.gridy=y; p.add(label("Empresa atual:", labelAlign), c);
        c.gridx=1; p.add(readOnly(empresaField), c); y++;

        c.gridx=0; c.gridy=y; p.add(label("Cidade:", labelAlign), c);
        c.gridx=1; p.add(readOnly(cidadeField), c); y++;

        c.gridx=0; c.gridy=y; p.add(label("Estado:", labelAlign), c);
        c.gridx=1; p.add(readOnly(estadoField), c); y++;

        c.gridx=0; c.gridy=y; p.add(label("País:", labelAlign), c);
        c.gridx=1; p.add(readOnly(paisField), c); y++;

        c.gridx=0; c.gridy=y; p.add(label("Exibir contato a docentes:", labelAlign), c);
        c.gridx=1; p.add(readOnly(visContatoField), c); y++;

        c.gridx=0; c.gridy=y; p.add(label("Exibir empresa a docentes:", labelAlign), c);
        c.gridx=1; p.add(readOnly(visEmpresaField), c); y++;

        return p;
    }

    private JLabel label(String txt, int align) {
        JLabel l = new JLabel(txt);
        l.setHorizontalAlignment(align);
        return l;
    }

    private JTextField readOnly(JTextField f) {
        f.setEditable(false);
        return f;
    }

    private JPanel buildEventosTab() {
        JPanel root = new JPanel(new BorderLayout(8,8));

        eventosTable.setAutoCreateRowSorter(true);
        root.add(new JScrollPane(eventosTable), BorderLayout.CENTER);

        return root;
    }

    private void carregarPerfil() {
        List<EgressoProfile> todos = egressosDao.listarTodos();
        EgressoProfile perfil = todos.stream()
                .filter(pp -> egresso.getId().equals(pp.getUsuarioId()))
                .findFirst()
                .orElseGet(() -> null);

        nomeField.setText(nullToEmpty(egresso.getNome()));
        emailField.setText(nullToEmpty(egresso.getEmail()));

        if (perfil != null) {
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
            empregadoField.setText(perfil.isEmpregado() ? "Sim" : "Não");
            empresaField.setText(nullToEmpty(perfil.getEmpresaAtual()));
            cidadeField.setText(nullToEmpty(perfil.getCidade()));
            estadoField.setText(nullToEmpty(perfil.getEstado()));
            paisField.setText(nullToEmpty(perfil.getPais()));
            visContatoField.setText(perfil.isPermitirExibirContato() ? "Sim" : "Não");
            visEmpresaField.setText(perfil.isPermitirExibirEmpresa() ? "Sim" : "Não");
        } else {
            dataNascField.setText("");
            anoField.setText("");
            githubField.setText("");
            rede1Field.setText("");
            rede2Field.setText("");
            cursoField.setText("");
            areaField.setText("");
            escolaridadeField.setText("");
            empregadoField.setText("");
            empresaField.setText("");
            cidadeField.setText("");
            estadoField.setText("");
            paisField.setText("");
            visContatoField.setText("");
            visEmpresaField.setText("");
        }
    }

    private void carregarEventos() {
        List<EventoChave> meus = eventosDao.listarTodos().stream()
                .filter(e -> egresso.getId().equals(e.getEgressoId()))
                .sorted(Comparator.comparing(EventoChave::getData,
                        java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())).reversed())
                .collect(Collectors.toList());

        eventosModel.setRowCount(0);
        for (EventoChave e : meus) {
            eventosModel.addRow(new Object[]{
                    e.getData() == null ? "" : DatasBR.format(e.getData()),
                    e.getTipo() == null ? "" : e.getTipo().name(),
                    e.getTitulo(),
                    e.getOrganizacao(),
                    e.getLocal()
            });
        }
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
