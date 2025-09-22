package br.com.egressos.infrastructure;

import br.com.egressos.domain.*;

import java.nio.file.Path;
import java.util.*;

public class UsuarioFileRepository implements UsuarioRepository {
    private final FileStorage storage;
    private final Serializer ser;
    private final Path file;

    public UsuarioFileRepository(FileStorage storage, Serializer ser, Path basePath) {
        this.storage = storage; this.ser = ser; this.file = basePath.resolve("usuarios.json");
    }

    private static class Box { public List<Usuario> usuarios = new ArrayList<>(); }

    private Box load() {
        byte[] b = storage.read(file);
        if (b.length==0) return new Box();
        return ser.deserialize(b, Box.class);
    }
    private void save(Box box){ storage.writeAtomic(file, ser.serialize(box)); }

    @Override public Optional<Usuario> findByEmail(String email) {
        return load().usuarios.stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).findFirst();
    }

    @Override public List<Usuario> list(String filtros) {
        // Filtros simples: "tipo=EGRESSO;ativo=true"
        Box box = load();
        if (filtros==null || filtros.isBlank()) return box.usuarios;
        Map<String,String> m = new HashMap<>();
        for (String p : filtros.split(";")) {
            String[] kv = p.split("="); if (kv.length==2) m.put(kv[0].trim().toLowerCase(), kv[1].trim());
        }
        return box.usuarios.stream().filter(u -> {
            boolean ok = true;
            if (m.containsKey("tipo")) ok &= u.getTipo().equalsIgnoreCase(m.get("tipo"));
            if (m.containsKey("ativo")) ok &= Boolean.toString(u.isAtivo()).equalsIgnoreCase(m.get("ativo"));
            return ok;
        }).toList();
    }

    @Override public void save(Usuario usuario) {
        Box box = load();
        box.usuarios.removeIf(u -> u.getEmail().equalsIgnoreCase(usuario.getEmail()));
        box.usuarios.add(usuario);
        save(box);
    }

    @Override public void delete(String email) {
        Box box = load();
        box.usuarios.removeIf(u -> u.getEmail().equalsIgnoreCase(email));
        save(box);
    }
}
