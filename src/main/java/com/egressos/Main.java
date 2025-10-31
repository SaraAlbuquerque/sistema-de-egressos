package com.egressos;

import com.egressos.auth.Passwords;
import com.egressos.dao.UsuariosDao;
import com.egressos.model.Papel;
import com.egressos.model.Usuario;
import com.egressos.ui.LoginFrame;

import javax.swing.*;
import java.time.Instant;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException ignored) {}
            
            try {
                UsuariosDao udao = new UsuariosDao();
                
                // Cria o coordenador padrão se ainda não existir
                if (udao.listar().isEmpty()) {
                    Usuario seed = new Usuario(
                            UUID.randomUUID().toString(),
                            "admin@fct.br",
                            "Coordenador",
                            Papel.COORDENADOR,
                            Passwords.sha256("admin123"),
                            false, // não exige troca de senha
                            Instant.now()
                    );
                    udao.salvarOuAtualizar(seed);
                    System.out.println("Usuário padrão criado: admin@fct.br / admin123");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        "Erro ao inicializar o sistema:\n" + ex.getMessage());
            }
            
            new LoginFrame().setVisible(true);
        });
    }
}
