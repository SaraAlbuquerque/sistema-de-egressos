package com.egressos.service;

import com.egressos.dao.EgressosDao;
import com.egressos.dao.EventosDao;
import com.egressos.dao.UsuariosDao;
import com.egressos.model.EgressoProfile;
import com.egressos.model.EventoChave;
import com.egressos.model.Usuario;

import java.io.FileWriter;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class ReportsService {
    private final UsuariosDao usuariosDao = new UsuariosDao();
    private final EgressosDao egressosDao = new EgressosDao();
    private final EventosDao eventosDao = new EventosDao();

    private Map<String, Usuario> mapUsuariosPorId() {
        return usuariosDao.listar().stream().collect(Collectors.toMap(Usuario::getId, u -> u));
    }

    public Path exportEmpregoCsv(Path destino) throws Exception {
        List<EgressoProfile> perfis = egressosDao.listarTodos();
        Map<String, Usuario> users = mapUsuariosPorId();
        try (FileWriter fw = new FileWriter(destino.toFile())) {
            fw.write("usuario_id,nome,email,empregado,empresa,cidade,estado,pais\n");
            for (EgressoProfile p : perfis) {
                Usuario u = users.getOrDefault(p.getUsuarioId(), null);
                fw.write(String.join(",",
                        safe(p.getUsuarioId()),
                        safe(u != null ? u.getNome() : ""),
                        safe(u != null ? u.getEmail() : ""),
                        p.isEmpregado() ? "1" : "0",
                        safe(p.getEmpresaAtual()),
                        safe(p.getCidade()),
                        safe(p.getEstado()),
                        safe(p.getPais())
                ));
                fw.write("\n");
            }
        }
        return destino;
    }

    public Path exportEscolaridadeCsv(Path destino) throws Exception {
        List<EgressoProfile> perfis = egressosDao.listarTodos();
        Map<String, Long> contagem = perfis.stream()
                .collect(Collectors.groupingBy(p -> nullToEmpty(p.getEscolaridadeAtual()).toUpperCase(), Collectors.counting()));
        try (FileWriter fw = new FileWriter(destino.toFile())) {
            fw.write("escolaridade,quantidade\n");
            for (var e : contagem.entrySet()) {
                fw.write(safe(e.getKey()) + "," + e.getValue() + "\n");
            }
        }
        return destino;
    }

    public Path exportAreasCsv(Path destino) throws Exception {
        List<EgressoProfile> perfis = egressosDao.listarTodos();
        Map<String, Long> contagem = perfis.stream()
                .collect(Collectors.groupingBy(p -> nullToEmpty(p.getAreaAtuacao()).toUpperCase(), Collectors.counting()));
        try (FileWriter fw = new FileWriter(destino.toFile())) {
            fw.write("area_atuacao,quantidade\n");
            for (var e : contagem.entrySet()) {
                fw.write(safe(e.getKey()) + "," + e.getValue() + "\n");
            }
        }
        return destino;
    }

    public Path exportLocaisCsv(Path destino) throws Exception {
        List<EgressoProfile> perfis = egressosDao.listarTodos();
        Map<String, Long> contagem = perfis.stream()
                .collect(Collectors.groupingBy(p ->
                        (nullToEmpty(p.getCidade()) + "/" + nullToEmpty(p.getEstado()) + "/" + nullToEmpty(p.getPais())).toUpperCase(),
                        Collectors.counting()));
        try (FileWriter fw = new FileWriter(destino.toFile())) {
            fw.write("local,quantidade\n");
            for (var e : contagem.entrySet()) {
                fw.write(safe(e.getKey()) + "," + e.getValue() + "\n");
            }
        }
        return destino;
    }

    public Path exportCrescimentoCsv(Path destino) throws Exception {
        List<EventoChave> todos = eventosDao.listarTodos().stream()
                .filter(e -> "TRABALHO".equalsIgnoreCase(nullToEmpty(e.getTipo())))
                .collect(Collectors.toList());
        Map<String, List<EventoChave>> porEgresso = todos.stream()
                .collect(Collectors.groupingBy(EventoChave::getEgressoId));

        try (FileWriter fw = new FileWriter(destino.toFile())) {
            fw.write("egresso_id,qtd_cargos,primeiro_cargo,ultimo_cargo,primeira_data,ultima_data,cresceu\n");
            for (var entry : porEgresso.entrySet()) {
                var lista = entry.getValue().stream()
                        .sorted(Comparator.comparing(EventoChave::getData))
                        .collect(Collectors.toList());
                String primeiroCargo = lista.isEmpty() ? "" : nullToEmpty(lista.get(0).getTitulo());
                String ultimoCargo = lista.isEmpty() ? "" : nullToEmpty(lista.get(lista.size()-1).getTitulo());
                String primeiraData = lista.isEmpty() ? "" : (lista.get(0).getData() == null ? "" : lista.get(0).getData().toString());
                String ultimaData   = lista.isEmpty() ? "" : (lista.get(lista.size()-1).getData() == null ? "" : lista.get(lista.size()-1).getData().toString());
                boolean cresceu = lista.size() >= 2 && !ultimoCargo.equalsIgnoreCase(primeiroCargo);
                fw.write(String.join(",",
                        safe(entry.getKey()),
                        String.valueOf(lista.size()),
                        safe(primeiroCargo),
                        safe(ultimoCargo),
                        safe(primeiraData),
                        safe(ultimaData),
                        cresceu ? "1" : "0"
                ));
                fw.write("\n");
            }
        }
        return destino;
    }
    
    public java.nio.file.Path exportEventosPorTipoCsv(java.nio.file.Path destino) throws Exception {
        java.util.List<com.egressos.model.EventoChave> eventos = eventosDao.listarTodos();
        java.util.Map<String, Long> cont = eventos.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        e -> nullToEmpty(e.getTipo()).toUpperCase(),
                        java.util.stream.Collectors.counting()));
        try (java.io.FileWriter fw = new java.io.FileWriter(destino.toFile())) {
            fw.write("tipo,quantidade\n");
            for (var e : cont.entrySet()) {
                fw.write(safe(e.getKey()) + "," + e.getValue() + "\n");
            }
        }
        return destino;
    }

    public java.nio.file.Path exportEventosPorOrganizacaoCsv(java.nio.file.Path destino) throws Exception {
        java.util.List<com.egressos.model.EventoChave> eventos = eventosDao.listarTodos();
        java.util.Map<String, Long> cont = eventos.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        e -> nullToEmpty(e.getOrganizacao()).toUpperCase(),
                        java.util.stream.Collectors.counting()));
        try (java.io.FileWriter fw = new java.io.FileWriter(destino.toFile())) {
            fw.write("organizacao,quantidade\n");
            for (var e : cont.entrySet()) {
                fw.write(safe(e.getKey()) + "," + e.getValue() + "\n");
            }
        }
        return destino;
    }

    private static String safe(String s) {
        if (s == null) return "";
        return s.replace(",", ";");
    }
    private static String nullToEmpty(String s) { return s == null ? "" : s; }
}
