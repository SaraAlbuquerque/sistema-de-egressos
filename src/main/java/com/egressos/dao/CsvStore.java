package com.egressos.dao;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class CsvStore {
    private static final CsvStore INSTANCE = new CsvStore();

    private final Path baseDir;
    private final Path filePath;
    private final String defaultHeader;

    private CsvStore() {
        this.baseDir = Paths.get("data");
        this.filePath = null;
        this.defaultHeader = null;
        try {
            if (!Files.exists(baseDir)) Files.createDirectories(baseDir);
        } catch (IOException e) {
            throw new RuntimeException("Falha criando diretório de dados: " + baseDir.toAbsolutePath(), e);
        }
    }

    public static CsvStore get() {
        return INSTANCE;
    }

    private Path resolveInData(String filename) {
        return baseDir.resolve(filename);
    }

    public List<String[]> readAll(String filename) {
        return readAllFromPath(resolveInData(filename));
    }

    public void writeAll(String filename, List<String[]> rows, String[] header) {
        writeAllToPath(resolveInData(filename), rows, header);
    }

    public CsvStore(Path filePath, String header) {
        this.baseDir = null;
        this.filePath = filePath;
        this.defaultHeader = header;
        ensureFileExistsWithHeader(filePath, header);
    }

    public List<String[]> readAll() {
        if (filePath == null)
            throw new IllegalStateException("CsvStore instance-based requerido: este objeto é o singleton.");
        return readAllFromPath(filePath);
    }

    public void overwrite(List<String[]> rows, String header) {
        if (filePath == null)
            throw new IllegalStateException("CsvStore instance-based requerido: este objeto é o singleton.");
        String[] hdr = toHeaderArray(header != null ? header : defaultHeader);
        writeAllToPath(filePath, rows, hdr);
    }

    private static void ensureFileExistsWithHeader(Path file, String header) {
        try {
            Path parent = file.getParent();
            if (parent != null && !Files.exists(parent)) Files.createDirectories(parent);
            if (!Files.exists(file)) {
                try (BufferedWriter bw = Files.newBufferedWriter(file, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                    if (header != null && !header.isBlank()) {
                        bw.write(header);
                        bw.newLine();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Falha preparando arquivo CSV: " + file.toAbsolutePath(), e);
        }
    }

    private static List<String[]> readAllFromPath(Path p) {
        List<String[]> out = new ArrayList<>();
        if (!Files.exists(p)) return out;

        try (BufferedReader br = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] cols = line.split(",", -1);
                if (first) {
                    first = false;
                    continue;
                }
                out.add(cols);
            }
        } catch (IOException e) {
            throw new RuntimeException("Falha lendo CSV: " + p.toAbsolutePath(), e);
        }
        return out;
    }

    private static void writeAllToPath(Path p, List<String[]> rows, String[] header) {
        try (BufferedWriter bw = Files.newBufferedWriter(p, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            if (header != null && header.length > 0) {
                bw.write(String.join(",", header));
                bw.newLine();
            }
            if (rows != null) {
                for (String[] r : rows) {
                    bw.write(String.join(",", r));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Falha escrevendo CSV: " + p.toAbsolutePath(), e);
        }
    }

    private static String[] toHeaderArray(String headerCsv) {
        if (headerCsv == null || headerCsv.isBlank()) return new String[0];
        return headerCsv.split(",", -1);
    }
}
