package com.egressos.service;

import com.egressos.dao.EventosDao;
import com.egressos.model.EventoChave;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportsService {
    private final EventosDao eventosDao = new EventosDao();

    private static String safe(String s){ return s==null? "" : s.replace("\n"," ").replace(","," "); }
    private static String n(String s){ return s==null? "" : s; }

    public java.nio.file.Path exportEventosPorTipoCsv(java.nio.file.Path destino) throws Exception {
        List<EventoChave> eventos = eventosDao.listarTodos();
        Map<String, Long> cont = eventos.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getTipo()==null? "" : e.getTipo().name(),
                        Collectors.counting()));
        try (java.io.FileWriter fw = new java.io.FileWriter(destino.toFile())) {
            fw.write("tipo,quantidade\n");
            for (var e : cont.entrySet()) {
                fw.write(safe(e.getKey()) + "," + e.getValue() + "\n");
            }
        }
        return destino;
    }

    public java.nio.file.Path exportEventosPorOrganizacaoCsv(java.nio.file.Path destino) throws Exception {
        List<EventoChave> eventos = eventosDao.listarTodos();
        Map<String, Long> cont = eventos.stream()
                .collect(Collectors.groupingBy(
                        e -> n(e.getOrganizacao()).toUpperCase(),
                        Collectors.counting()));
        try (java.io.FileWriter fw = new java.io.FileWriter(destino.toFile())) {
            fw.write("organizacao,quantidade\n");
            for (var e : cont.entrySet()) {
                fw.write(safe(e.getKey()) + "," + e.getValue() + "\n");
            }
        }
        return destino;
    }

    public java.nio.file.Path exportEventosPorPeriodoCsv(java.nio.file.Path destino, LocalDate inicio, LocalDate fim) throws Exception {
        List<EventoChave> evs = eventosDao.listarTodos();
        try (java.io.FileWriter fw = new java.io.FileWriter(destino.toFile())) {
            fw.write("data,tipo,titulo,organizacao,local\n");
            for (EventoChave e : evs){
                if (e.getData()==null) continue;
                if (inicio!=null && e.getData().isBefore(inicio)) continue;
                if (fim!=null && e.getData().isAfter(fim)) continue;
                fw.write(String.join(",",
                        safe(e.getData().toString()),
                        safe(e.getTipo()==null? "" : e.getTipo().name()),
                        safe(e.getTitulo()),
                        safe(e.getOrganizacao()),
                        safe(e.getLocal())) + "\n");
            }
        }
        return destino;
    }


    public java.nio.file.Path exportEventosPorLocalCsv(java.nio.file.Path destino, String localFiltro) throws Exception {
        List<EventoChave> evs = eventosDao.listarTodos();
        String lf = localFiltro==null? "" : localFiltro.trim().toLowerCase();
        try (java.io.FileWriter fw = new java.io.FileWriter(destino.toFile())) {
            fw.write("data,tipo,titulo,organizacao,local\n");
            for (EventoChave e : evs){
                String loc = n(e.getLocal());
                if (!lf.isEmpty() && !loc.toLowerCase().contains(lf)) continue;
                fw.write(String.join(",",
                        safe(e.getData()==null? "" : e.getData().toString()),
                        safe(e.getTipo()==null? "" : e.getTipo().name()),
                        safe(e.getTitulo()),
                        safe(e.getOrganizacao()),
                        safe(loc)) + "\n");
            }
        }
        return destino;
    }
}
