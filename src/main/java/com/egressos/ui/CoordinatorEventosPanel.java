package com.egressos.ui;

import com.egressos.dao.EventosDao;
import com.egressos.dao.UsuariosDao;
import com.egressos.model.EventoChave;
import com.egressos.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class CoordinatorEventosPanel extends JPanel {
    private final EventosDao eventosDao = new EventosDao();
    private final UsuariosDao usuariosDao = new UsuariosDao();

    private final JTextField tipoField = new JTextField();
    private final JTextField orgField = new JTextField();
    private final JTextField localField = new JTextField();
    private final JTextField dataDeField = new JTextField();
    private final JTextField dataAteField = new JTextField();
    private final JTextField textoField = new JTextField();

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Data","Egresso","Tipo","Título","Organização","Local"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public CoordinatorEventosPanel() {
        super(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel filtros = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        int y = 0;

        c.gridx=0; c.gridy=y; filtros.add(new JLabel("Tipo:"), c);
        c.gridx=1; filtros.add(tipoField, c);
        c.gridx=2; filtros.add(new JLabel("Organização:"), c);
        c.gridx=3; filtros.add(orgField, c);

        y++;
        c.gridx=0; c.gridy=y; filtros.add(new JLabel("Local:"), c);
        c.gridx=1; filtros.add(localField, c);
        c.gridx=2; filtros.add(new JLabel("Data de (YYYY-MM-DD):"), c);
        c.gridx=3; filtros.add(dataDeField, c);

        y++;
        c.gridx=0; c.gridy=y; filtros.add(new JLabel("Data até (YYYY-MM-DD):"), c);
        c.gridx=1; filtros.add(dataAteField, c);
        c.gridx=2; filtros.add(new JLabel("Texto (título/descrição):"), c);
        c.gridx=3; filtros.add(textoField, c);

        JButton buscar = new JButton("Filtrar");
        JButton limpar = new JButton("Limpar");
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acoes.add(limpar); acoes.add(buscar);

        add(filtros, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(acoes, BorderLayout.SOUTH);

        table.setAutoCreateRowSorter(true);

        buscar.addActionListener(e -> carregar());
        limpar.addActionListener(e -> { tipoField.setText(""); orgField.setText(""); localField.setText("");
            dataDeField.setText(""); dataAteField.setText(""); textoField.setText(""); carregar(); });

        carregar();
    }

    private void carregar() {
        try {
            Map<String, String> nomes = usuariosDao.listar().stream()
                    .collect(Collectors.toMap(Usuario::getId, Usuario::getNome));

            List<EventoChave> eventos = eventosDao.listarTodos().stream().filter(ev -> {
                if (!tipoField.getText().trim().isEmpty() &&
                        (ev.getTipo()==null || !ev.getTipo().toLowerCase().contains(tipoField.getText().trim().toLowerCase()))) return false;
                if (!orgField.getText().trim().isEmpty() &&
                        (ev.getOrganizacao()==null || !ev.getOrganizacao().toLowerCase().contains(orgField.getText().trim().toLowerCase()))) return false;
                if (!localField.getText().trim().isEmpty() &&
                        (ev.getLocal()==null || !ev.getLocal().toLowerCase().contains(localField.getText().trim().toLowerCase()))) return false;
                if (!textoField.getText().trim().isEmpty()) {
                    String t = textoField.getText().trim().toLowerCase();
                    String blob = (n(ev.getTitulo())+" "+n(ev.getDescricao())).toLowerCase();
                    if (!blob.contains(t)) return false;
                }
                if (!dataDeField.getText().trim().isEmpty()) {
                    try { if (ev.getData().isBefore(LocalDate.parse(dataDeField.getText().trim()))) return false; } catch (Exception ignore) {}
                }
                if (!dataAteField.getText().trim().isEmpty()) {
                    try { if (ev.getData().isAfter(LocalDate.parse(dataAteField.getText().trim()))) return false; } catch (Exception ignore) {}
                }
                return true;
            }).sorted(Comparator.comparing(EventoChave::getData).reversed()).collect(Collectors.toList());

            model.setRowCount(0);
            for (EventoChave ev : eventos) {
                model.addRow(new Object[]{
                        ev.getData()!=null? ev.getData().format(DF) : "",
                        nomes.getOrDefault(ev.getEgressoId(), ev.getEgressoId()),
                        ev.getTipo(),
                        ev.getTitulo(),
                        ev.getOrganizacao(),
                        ev.getLocal()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar eventos: " + ex.getMessage());
        }
    }

    private String n(String s) { return s==null? "" : s; }
}
