package com.egressos.view;

import com.egressos.dao.EventosDao;
import com.egressos.dao.UsuariosDao;
import com.egressos.model.EventoChave;
import com.egressos.model.TipoEvento;
import com.egressos.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CoordinatorEventosPanel extends JPanel {

    private final EventosDao eventosDao = new EventosDao();
    private final UsuariosDao usuariosDao = new UsuariosDao();

    private final JComboBox<String> tipoCombo = new JComboBox<>();
    private final JTextField orgField   = new JTextField();
    private final JTextField localField = new JTextField();
    private final JTextField dataDeField  = new JTextField();
    private final JTextField dataAteField = new JTextField();
    private final JTextField textoField   = new JTextField();

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Data","Egresso","Tipo","Título","Organização","Local"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    private final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public CoordinatorEventosPanel() {
        super(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        tipoCombo.addItem(""); // qualquer tipo
        for (TipoEvento t : TipoEvento.values()) {
            tipoCombo.addItem(t.name());
        }

        aplicarMascaraDataBR(dataDeField);
        aplicarMascaraDataBR(dataAteField);

        JPanel filtros = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        int y = 0;

        c.gridx=0; c.gridy=y; filtros.add(new JLabel("Tipo:"), c);
        c.gridx=1; filtros.add(tipoCombo, c);
        c.gridx=2; filtros.add(new JLabel("Organização:"), c);
        c.gridx=3; filtros.add(orgField, c);

        y++;
        c.gridx=0; c.gridy=y; filtros.add(new JLabel("Local:"), c);
        c.gridx=1; filtros.add(localField, c);
        c.gridx=2; filtros.add(new JLabel("Data de:"), c);
        c.gridx=3; filtros.add(dataDeField, c);

        y++;
        c.gridx=0; c.gridy=y; filtros.add(new JLabel("Data até:"), c);
        c.gridx=1; filtros.add(dataAteField, c);
        c.gridx=2; filtros.add(new JLabel("Texto (título/descrição):"), c);
        c.gridx=3; filtros.add(textoField, c);

        JButton buscar = new JButton("Filtrar");
        JButton limpar = new JButton("Limpar");

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acoes.add(limpar);
        acoes.add(buscar);

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(filtros, BorderLayout.CENTER);
        topo.add(acoes, BorderLayout.SOUTH);

        add(topo, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        table.setAutoCreateRowSorter(true);

        buscar.addActionListener(e -> recarregar());
        limpar.addActionListener(e -> {
            tipoCombo.setSelectedIndex(0);
            orgField.setText("");
            localField.setText("");
            dataDeField.setText("");
            dataAteField.setText("");
            textoField.setText("");
            recarregar();
        });

        recarregar();
    }

    public void recarregar() {

        LocalDate ini = null;
        LocalDate fim = null;

        String sDe  = dataDeField.getText().trim();
        String sAte = dataAteField.getText().trim();

        if (!sDe.isEmpty()) {
            if (sDe.length() != 10) {
                JOptionPane.showMessageDialog(this,
                        "Data inicial inválida. Use o formato dd/MM/yyyy.",
                        "Período inválido",
                        JOptionPane.WARNING_MESSAGE);
                dataDeField.requestFocus();
                return;
            }
            try {
                ini = LocalDate.parse(sDe, DF);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Data inicial inválida. Use o formato dd/MM/yyyy.",
                        "Período inválido",
                        JOptionPane.WARNING_MESSAGE);
                dataDeField.requestFocus();
                return;
            }
        }

        if (!sAte.isEmpty()) {
            if (sAte.length() != 10) {
                JOptionPane.showMessageDialog(this,
                        "Data final inválida. Use o formato dd/MM/yyyy.",
                        "Período inválido",
                        JOptionPane.WARNING_MESSAGE);
                dataAteField.requestFocus();
                return;
            }
            try {
                fim = LocalDate.parse(sAte, DF);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Data final inválida. Use o formato dd/MM/yyyy.",
                        "Período inválido",
                        JOptionPane.WARNING_MESSAGE);
                dataAteField.requestFocus();
                return;
            }
        }

        if (ini != null && fim != null && ini.isAfter(fim)) {
            JOptionPane.showMessageDialog(this,
                    "Período inválido: a data inicial é maior que a data final.",
                    "Período inválido",
                    JOptionPane.WARNING_MESSAGE);
            dataDeField.requestFocus();
            return;
        }

        final LocalDate iniFinal = ini;
        final LocalDate fimFinal = fim;
        final String tipoSelecionado =
                tipoCombo.getSelectedItem() == null ? "" : tipoCombo.getSelectedItem().toString().trim();

        try {
            Map<String, String> nomes = usuariosDao.listar().stream()
                    .collect(Collectors.toMap(Usuario::getId, Usuario::getNome));

            List<EventoChave> eventos = eventosDao.listarTodos().stream()
                    .filter(ev -> {

                        if (!tipoSelecionado.isEmpty()) {
                            if (ev.getTipo() == null) return false;
                            if (!ev.getTipo().name().equalsIgnoreCase(tipoSelecionado)) return false;
                        }

                        if (!orgField.getText().isBlank() &&
                                (ev.getOrganizacao() == null ||
                                        !ev.getOrganizacao().toLowerCase()
                                                .contains(orgField.getText().toLowerCase())))
                            return false;

                        if (!localField.getText().isBlank() &&
                                (ev.getLocal() == null ||
                                        !ev.getLocal().toLowerCase()
                                                .contains(localField.getText().toLowerCase())))
                            return false;

                        if (!textoField.getText().isBlank()) {
                            String t = textoField.getText().trim().toLowerCase();
                            String blob = (n(ev.getTitulo()) + " " + n(ev.getDescricao())).toLowerCase();
                            if (!blob.contains(t)) return false;
                        }

                        if (iniFinal != null) {
                            LocalDate d = ev.getData();
                            if (d == null || d.isBefore(iniFinal)) return false;
                        }

                        if (fimFinal != null) {
                            LocalDate d = ev.getData();
                            if (d == null || d.isAfter(fimFinal)) return false;
                        }

                        return true;
                    })
                    .sorted(Comparator.comparing(EventoChave::getData,
                            Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                    .collect(Collectors.toList());

            model.setRowCount(0);

            for (EventoChave e : eventos) {
                model.addRow(new Object[] {
                        e.getData() != null ? e.getData().format(DF) : "",
                        nomes.getOrDefault(e.getEgressoId(), e.getEgressoId()),
                        e.getTipo() != null ? e.getTipo().name() : "",
                        e.getTitulo(),
                        e.getOrganizacao(),
                        e.getLocal()
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar eventos: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aplicarMascaraDataBR(JTextField campo) {
        AbstractDocument doc = (AbstractDocument) campo.getDocument();

        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb,int off,int len,String txt,AttributeSet a)
                    throws BadLocationException {

                String v = fb.getDocument().getText(0, fb.getDocument().getLength());
                v = v.substring(0,off) + txt + v.substring(off+len);
                v = v.replaceAll("[^0-9]","");

                if (v.length() > 8) return; // ddMMyyyy

                StringBuilder out = new StringBuilder();
                for (int i = 0; i < v.length(); i++) {
                    out.append(v.charAt(i));
                    if (i == 1 || i == 3) out.append('/');
                }

                fb.remove(0, fb.getDocument().getLength());
                fb.insertString(0, out.toString(), a);
            }
        });
    }

    private String n(String s){ return s==null ? "" : s; }
}
