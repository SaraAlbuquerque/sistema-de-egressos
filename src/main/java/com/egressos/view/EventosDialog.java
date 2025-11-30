package com.egressos.view;

import com.egressos.dao.EventosDao;
import com.egressos.model.EventoChave;
import com.egressos.model.TipoEvento;
import com.egressos.model.Usuario;
import com.egressos.util.DatasBR;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
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
    private final JTextField dataField = new JTextField(); // dd/MM/yyyy
    private final JTextArea descArea = new JTextArea(4, 20);

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Data","Tipo","Título","Org.","Local"}, 0){
        @Override public boolean isCellEditable(int r,int c){ return false; }
    };
    private final JTable table = new JTable(model);

    public EventosDialog(Frame owner, Usuario egresso){
        super(owner, "Eventos-chave", true);
        this.egresso = egresso;

        setSize(900, 650);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(owner);

        aplicarMascaraDataBR(dataField);

        Dimension campoBaixo = new Dimension(200, 24);
        Font f = tituloField.getFont().deriveFont(12f);

        tipoCombo.setPreferredSize(campoBaixo);
        tituloField.setPreferredSize(campoBaixo);
        orgField.setPreferredSize(campoBaixo);
        localField.setPreferredSize(campoBaixo);
        dataField.setPreferredSize(campoBaixo);

        tipoCombo.setFont(f);
        tituloField.setFont(f);
        orgField.setFont(f);
        localField.setFont(f);
        dataField.setFont(f);
        descArea.setFont(f);


        JPanel form = new JPanel(new GridLayout(0,2,6,6));
        form.add(new JLabel("Tipo:")); form.add(tipoCombo);
        form.add(new JLabel("Título:")); form.add(tituloField);
        form.add(new JLabel("Organização:")); form.add(orgField);
        form.add(new JLabel("Local:")); form.add(localField);
        form.add(new JLabel("Data (dd/MM/yyyy):")); form.add(dataField);
        form.add(new JLabel("Descrição (opcional):"));
        JScrollPane sp = new JScrollPane(descArea);
        form.add(sp);

        JButton salvar = new JButton("Salvar");
        JButton cancelar = new JButton("Cancelar");

        salvar.addActionListener(e -> salvarEvento());
        cancelar.addActionListener(e -> dispose());

        JPanel top = new JPanel(new BorderLayout(8,8));
        top.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(cancelar);
        actions.add(salvar);
        top.add(actions, BorderLayout.SOUTH);

        table.setAutoCreateRowSorter(true);
        var sorter = new javax.swing.table.TableRowSorter<>(model);
        table.setRowSorter(sorter);
        var ks = new ArrayList<javax.swing.RowSorter.SortKey>();
        ks.add(new javax.swing.RowSorter.SortKey(0, javax.swing.SortOrder.DESCENDING)); // 0 = "Data"
        sorter.setSortKeys(ks);


        JPanel root = new JPanel(new BorderLayout(8,8));
        root.setBorder(BorderFactory.createEmptyBorder(10,10,10,10)); // padding da janela

        root.add(top, BorderLayout.NORTH);
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        setContentPane(root);

        carregarTabela();
    }

    private void carregarTabela(){
        model.setRowCount(0);
        List<EventoChave> meus = new ArrayList<>();
        for (EventoChave e : eventosDao.listarTodos()){
            if (egresso.getId().equals(e.getEgressoId())) meus.add(e);
        }
        meus.sort(Comparator.comparing(EventoChave::getData,
                Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        for (EventoChave e : meus){
            model.addRow(new Object[]{
                    e.getData()==null? "" : DatasBR.format(e.getData()),
                    e.getTipo()==null? "" : e.getTipo().name(),
                    e.getTitulo(),
                    e.getOrganizacao(),
                    e.getLocal()
            });
        }
    }

    private void salvarEvento(){
        Object tipoObj = tipoCombo.getSelectedItem();
        String titulo  = tituloField.getText().trim();
        String org     = orgField.getText().trim();
        String local   = localField.getText().trim();
        String sData   = dataField.getText().trim();
        String desc    = descArea.getText().trim(); // opcional

        if (tipoObj == null) {
            JOptionPane.showMessageDialog(this, "Selecione o tipo de evento.");
            return;
        }
        if (titulo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o título do evento.");
            return;
        }
        if (org.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe a organização.");
            return;
        }
        if (local.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o local.");
            return;
        }

        if (sData.isEmpty() || sData.length() != 10) {
            JOptionPane.showMessageDialog(this,
                    "Informe a data no formato dd/MM/yyyy.");
            return;
        }

        LocalDate data = null;
        try {
            data = DatasBR.parse(sData);
        } catch (Exception ex) {
            data = null;
        }
        if (data == null) {
            JOptionPane.showMessageDialog(this,
                    "Data inválida. Use o formato dd/MM/yyyy e uma data existente.");
            return;
        }

        TipoEvento tipo = (TipoEvento) tipoObj;

        try {
            List<EventoChave> all = eventosDao.listarTodos();
            EventoChave e = new EventoChave();
            e.setId(java.util.UUID.randomUUID().toString());
            e.setEgressoId(egresso.getId());
            e.setTipo(tipo);
            e.setTitulo(titulo);
            e.setOrganizacao(org);
            e.setLocal(local);
            e.setDescricao(desc.isEmpty() ? null : desc);
            e.setData(data);

            all.add(e);
            eventosDao.salvarTodos(all);

            JOptionPane.showMessageDialog(this, "Evento salvo com sucesso.");
            dispose();

        } catch (Exception ex){
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar evento: " + ex.getMessage());
        }
    }

    private void aplicarMascaraDataBR(JTextField campo){
        AbstractDocument doc = (AbstractDocument) campo.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int off, int len, String txt, AttributeSet a)
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
}
