package br.com.egressos.infrastructure;

import java.io.IOException;
import java.nio.file.*;

public class FileStorage {
    public byte[] read(Path path) {
        try {
            if (!Files.exists(path)) return new byte[0];
            return Files.readAllBytes(path);
        } catch (IOException e) { throw new RuntimeException(e); }
    }
    public void writeAtomic(Path path, byte[] data) {
        try {
            Files.createDirectories(path.getParent());
            Path tmp = path.resolveSibling(path.getFileName()+".tmp");
            Files.write(tmp, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) { throw new RuntimeException(e); }
    }
}
