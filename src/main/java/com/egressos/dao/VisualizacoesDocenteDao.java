package com.egressos.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CSV: visualizacoes_docente.csv
 * Header: docenteId,egressoUsuarioId,ts
 */
public class VisualizacoesDocenteDao {
    private final CsvStore csv = CsvStore.get();

    public static class Row {
        public String docenteId;
        public String egressoUsuarioId;
        public LocalDateTime ts;
    }

    public void registrar(String docenteId, String egressoUsuarioId){
        List<String[]> all = csv.readAll("visualizacoes_docente.csv");
        all.add(new String[]{ docenteId, egressoUsuarioId, LocalDateTime.now().toString() });
        csv.writeAll("visualizacoes_docente.csv", all, new String[]{"docenteId","egressoUsuarioId","ts"});
    }

    public List<Row> ultimos(String docenteId, int limit){
        List<Row> rows = new ArrayList<>();
        for (String[] r : csv.readAll("visualizacoes_docente.csv")){
            if (r.length<3) continue;
            if (!docenteId.equals(r[0])) continue;
            Row row = new Row();
            row.docenteId = r[0];
            row.egressoUsuarioId = r[1];
            row.ts = LocalDateTime.parse(r[2]);
            rows.add(row);
        }
        return rows.stream()
                .sorted((a,b) -> b.ts.compareTo(a.ts))
                .limit(limit)
                .collect(Collectors.toList());
    }
}
