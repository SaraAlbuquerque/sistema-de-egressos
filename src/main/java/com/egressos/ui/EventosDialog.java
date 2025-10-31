package com.egressos.ui;

import com.egressos.model.EventoChave;
import com.egressos.model.Usuario;
import com.egressos.service.EventosService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class EventosDialog extends JDialog {
    private final EventosService service = new EventosService();
    private final Usuario egresso;
    private final DefaultTableModel model =
    new DefaultTableModel(new Object[]{"ID","Data","Tipo","Título","Org","Local"}, 0);

    public EventosDialog(Frame owner, Usuario egresso) {
        super(owner, "Eventos-chave", true);
        this.egresso = egresso;
        setSize(800, 500);
        setLocationRelativeTo(owner);

        JTable table = new JTable(model);
        JButton add = new JButton("Novo");
        JButton edit = new JButton("Editar");
        JButton del = new JButton("Excluir");
        JPanel bar = new JPanel(); bar.add(add); bar.add(edit); bar.add(del);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bar, BorderLayout.SOUTH);
        
        table.setAutoCreateRowSorter(true);

        javax.swing.table.TableRowSorter<javax.swing.table.DefaultTableModel> sorter = new javax.swing.table.TableRowSorter<>(model);
        table.setRowSorter(sorter);
        java.util.List<javax.swing.RowSorter.SortKey> ks = new java.util.ArrayList<>();
        ks.add(new javax.swing.RowSorter.SortKey(6, javax.swing.SortOrder.DESCENDING));
        sorter.setSortKeys(ks);

        Runnable refresh = () -> {
            model.setRowCount(0);
            List<EventoChave> lista = service.listarPorEgresso(egresso.getId());
            for (EventoChave e : lista) {
                model.addRow(new Object[]{e.getId(), e.getData(), e.getTipo(), e.getTitulo(),
                        e.getOrganizacao(), e.getLocal()});
            }
        };
        refresh.run();

        add.addActionListener(ev -> {
            EventoChave e = formEvento(null);
            if (e != null) { service.criar(egresso.getId(), e.getTipo(), e.getTitulo(), e.getDescricao(),
                    e.getOrganizacao(), e.getData(), e.getLocal(), e.getObservacoes()); refresh.run(); }
        });

        edit.addActionListener(ev -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            String id = (String) model.getValueAt(row, 0);
            // carrega da lista atual
            EventoChave existente = service.listarPorEgresso(egresso.getId())
                    .stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
            EventoChave nov = formEvento(existente);
            if (nov != null) {
                nov.setId(id); nov.setEgressoId(egresso.getId());
                service.atualizar(nov); refresh.run();
            }
        });

        del.addActionListener(ev -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            String id = (String) model.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this,"Excluir evento?","Confirma",
                    JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION) {
                service.excluir(id); refresh.run();
            }
        });
    }

    private EventoChave formEvento(EventoChave base) {
        JTextField data = new JTextField(base==null? "2025-01-01" : base.getData().toString());
        JTextField tipo = new JTextField(base==null? "TRABALHO" : base.getTipo());
        JTextField titulo = new JTextField(base==null? "" : base.getTitulo());
        JTextField org = new JTextField(base==null? "" : base.getOrganizacao());
        JTextField local = new JTextField(base==null? "" : base.getLocal());
        JTextField desc = new JTextField(base==null? "" : base.getDescricao());
        JTextField obs = new JTextField(base==null? "" : base.getObservacoes());

        JPanel p = new JPanel(new GridLayout(7,2,6,6));
        p.add(new JLabel("Data (YYYY-MM-DD):")); p.add(data);
        p.add(new JLabel("Tipo:")); p.add(tipo);
        p.add(new JLabel("Título:")); p.add(titulo);
        p.add(new JLabel("Organização:")); p.add(org);
        p.add(new JLabel("Local:")); p.add(local);
        p.add(new JLabel("Descrição:")); p.add(desc);
        p.add(new JLabel("Observações:")); p.add(obs);

        int r = JOptionPane.showConfirmDialog(this, p, "Evento", JOptionPane.OK_CANCEL_OPTION);
        if (r != JOptionPane.OK_OPTION) return null;

        EventoChave e = base==null? new EventoChave() : base;
        e.setTipo(tipo.getText().trim());
        e.setTitulo(titulo.getText().trim());
        e.setDescricao(desc.getText().trim());
        e.setOrganizacao(org.getText().trim());
        e.setLocal(local.getText().trim());
        e.setObservacoes(obs.getText().trim());
        e.setData(LocalDate.parse(data.getText().trim()));
        if (base==null) e.setId(EventoChave.newId());
        return e;
    }
}
