package com.egressos.ui;

import com.egressos.dao.EventosDao;
import com.egressos.model.EventoChave;
import com.egressos.model.TipoEvento;
import com.egressos.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EventosDialog extends JDialog {
    private final EventosDao eventosDao = new EventosDao();
    private final Usuario egresso;

    private final JComboBox<TipoEvento> tipoCombo = new JComboBox<>(TipoEvento.values());
    private final JTextField tituloField = new JTextField();
    private final JTextField orgField = new JTextField();
    private final JTextField localField = new JTextField();
    private final JTextField dataField = new JTextField("YYYY-MM-DD");
    private final JTextArea descArea = new JTextArea(4, 20);

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID","Data","Tipo","Título","Org.","Local"}, 0){
        @Override public boolean isCellEditable(int r,int c){ return false; }
    };
    private final JTable table = new JTable(model);

    public EventosDialog(Frame owner, Usuario egresso){
        super(owner, "Eventos-chave", true);
        this.egresso = egresso;
        setSize(800, 500);
        setLocationRelativeTo(owner);

        JPanel form = new JPanel(new GridLayout(0,2,6,6));
        form.add(new JLabel("Tipo:")); form.add(tipoCombo);
        form.add(new JLabel("Título:")); form.add(tituloField);
        form.add(new JLabel("Organização:")); form.add(orgField);
        form.add(new JLabel("Local:")); form.add(localField);
        form.add(new JLabel("Data (YYYY-MM-DD):")); form.add(dataField);
        form.add(new JLabel("Descrição:"));
        JScrollPane sp = new JScrollPane(descArea);
        form.add(sp);

        JButton salvar = new JButton("Salvar");
        JButton remover = new JButton("Remover Selecionado");

        salvar.addActionListener(e -> salvarEvento());
        remover.addActionListener(e -> removerSelecionado());

        JPanel top = new JPanel(new BorderLayout(8,8));
        top.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(salvar); actions.add(remover);
        top.add(actions, BorderLayout.SOUTH);

        table.setAutoCreateRowSorter(true);
        var sorter = new javax.swing.table.TableRowSorter<>(model);
        table.setRowSorter(sorter);
        var ks = new java.util.ArrayList<javax.swing.RowSorter.SortKey>();
        // coluna 1 = "Data"
        ks.add(new javax.swing.RowSorter.SortKey(1, javax.swing.SortOrder.DESCENDING));
        sorter.setSortKeys(ks);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        carregarTabela();
    }

    private void carregarTabela(){
        model.setRowCount(0);
        List<EventoChave> meus = new ArrayList<>();
        for (EventoChave e : eventosDao.listarTodos()){
            if (egresso.getId().equals(e.getEgressoId())) meus.add(e);
        }
        meus.sort(Comparator.comparing(EventoChave::getData, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        for (EventoChave e : meus){
            model.addRow(new Object[]{
                    e.getId(),
                    e.getData()==null? "" : e.getData().toString(),
                    e.getTipo()==null? "" : e.getTipo().name(),
                    e.getTitulo(),
                    e.getOrganizacao(),
                    e.getLocal()
            });
        }
    }

    private void salvarEvento(){
        List<EventoChave> all = eventosDao.listarTodos();
        EventoChave e = new EventoChave();
        e.setId(java.util.UUID.randomUUID().toString());
        e.setEgressoId(egresso.getId());
        e.setTipo((TipoEvento) tipoCombo.getSelectedItem());
        e.setTitulo(tituloField.getText().trim());
        e.setOrganizacao(orgField.getText().trim());
        e.setLocal(localField.getText().trim());
        e.setDescricao(descArea.getText().trim());
        try {
            String ds = dataField.getText().trim();
            e.setData(ds.isBlank()? null : LocalDate.parse(ds));
        } catch (Exception ex){
            JOptionPane.showMessageDialog(this, "Data inválida. Use YYYY-MM-DD.");
            return;
        }
        all.add(e);
        eventosDao.salvarTodos(all);
        carregarTabela();
        limparForm();
    }

    private void removerSelecionado(){
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione uma linha."); return; }
        int m = table.convertRowIndexToModel(row);
        String id = String.valueOf(model.getValueAt(m, 0));
        List<EventoChave> all = eventosDao.listarTodos();
        all.removeIf(ev -> id.equals(ev.getId()));
        eventosDao.salvarTodos(all);
        carregarTabela();
    }

    private void limparForm(){
        tipoCombo.setSelectedItem(TipoEvento.OUTRO);
        tituloField.setText("");
        orgField.setText("");
        localField.setText("");
        dataField.setText("YYYY-MM-DD");
        descArea.setText("");
    }
}
