package com.egressos.dao;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class CsvStore {
    private final Path path;

    public CsvStore(Path path, String header) {
        this.path = path;
        try {
            if (Files.notExists(path.getParent())) Files.createDirectories(path.getParent());
            if (Files.notExists(path)) {
                Files.write(path, (header + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro criando arquivo CSV: " + path, e);
        }
    }

    public synchronized List<String[]> readAll() {
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            List<String[]> rows = new ArrayList<>();
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; } // pula header
                if (line.isEmpty()) continue;
                rows.add(parse(line));
            }
            return rows;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void overwrite(List<String[]> rows, String header) {
        try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "rw");
             FileChannel ch = raf.getChannel();
             FileLock lock = ch.lock()) {
            raf.setLength(0);
            raf.write((header + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
            for (String[] r : rows) {
                raf.write((join(r) + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String[] parse(String line) {
        return line.split(",", -1);
    }
    private static String join(String[] cols) {
        return String.join(",", cols);
    }
}
