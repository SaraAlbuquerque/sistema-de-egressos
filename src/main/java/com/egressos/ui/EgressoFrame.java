package com.egressos.ui;

import com.egressos.model.EgressoProfile;
import com.egressos.model.Usuario;
import com.egressos.service.EgressosService;

import javax.swing.*;
import java.awt.*;

public class EgressoFrame extends JFrame {
    private final EgressosService service = new EgressosService();

    public EgressoFrame(Usuario egresso) {
        super("Egressos — Meu Perfil");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 520);
        setLocationRelativeTo(null);

        JMenuBar bar = new JMenuBar();
        JMenu arquivo = new JMenu("Arquivo");
        JMenuItem trocar = new JMenuItem("Trocar de usuário");
        trocar.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        JMenuItem sair = new JMenuItem("Sair");
        sair.addActionListener(e -> System.exit(0));
        arquivo.add(trocar); arquivo.addSeparator(); arquivo.add(sair);
        bar.add(arquivo);
        setJMenuBar(bar);

        JTextField curso = new JTextField();
        JTextField ano = new JTextField();
        JTextField area = new JTextField();
        JTextField escolaridade = new JTextField();
        JCheckBox empregado = new JCheckBox("Empregado");
        JTextField empresa = new JTextField();
        JTextField cidade = new JTextField();
        JTextField estado = new JTextField();
        JTextField pais = new JTextField();

        JButton salvar = new JButton("Salvar");
        JButton eventos = new JButton("Eventos-chave…");

        JPanel p = new JPanel(new GridLayout(11,2,6,6));
        p.add(new JLabel("Curso:")); p.add(curso);
        p.add(new JLabel("Ano de formação:")); p.add(ano);
        p.add(new JLabel("Área de atuação:")); p.add(area);
        p.add(new JLabel("Escolaridade atual:")); p.add(escolaridade);
        p.add(new JLabel("Status:")); p.add(empregado);
        p.add(new JLabel("Empresa atual:")); p.add(empresa);
        p.add(new JLabel("Cidade:")); p.add(cidade);
        p.add(new JLabel("Estado:")); p.add(estado);
        p.add(new JLabel("País:")); p.add(pais);
        p.add(salvar); p.add(eventos);
        add(new JScrollPane(p));

        service.obter(egresso.getId()).ifPresent(ep -> {
            curso.setText(ep.getCurso());
            ano.setText(String.valueOf(ep.getAnoFormacao()));
            area.setText(ep.getAreaAtuacao());
            escolaridade.setText(ep.getEscolaridadeAtual());
            empregado.setSelected(ep.isEmpregado());
            empresa.setText(ep.getEmpresaAtual());
            cidade.setText(ep.getCidade());
            estado.setText(ep.getEstado());
            pais.setText(ep.getPais());
        });

        salvar.addActionListener(e -> {
            try {
                EgressoProfile ep = new EgressoProfile();
                ep.setUsuarioId(egresso.getId());
                ep.setCurso(curso.getText().trim());
                ep.setAnoFormacao(Integer.parseInt(ano.getText().trim()));
                ep.setAreaAtuacao(area.getText().trim());
                ep.setEscolaridadeAtual(escolaridade.getText().trim());
                ep.setEmpregado(empregado.isSelected());
                ep.setEmpresaAtual(empresa.getText().trim());
                ep.setCidade(cidade.getText().trim());
                ep.setEstado(estado.getText().trim());
                ep.setPais(pais.getText().trim());
                service.salvar(ep);
                JOptionPane.showMessageDialog(this, "Perfil salvo.");
            } catch (HeadlessException | NumberFormatException ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
        });

        eventos.addActionListener(e -> new EventosDialog(this, egresso).setVisible(true));
    }
}
