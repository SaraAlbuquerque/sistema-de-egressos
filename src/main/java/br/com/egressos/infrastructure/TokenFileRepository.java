package br.com.egressos.infrastructure;

import br.com.egressos.domain.*;

import java.nio.file.Path;
import java.util.*;

public class TokenFileRepository implements TokenRepository {
    private final FileStorage storage;
    private final Serializer ser;
    private final Path file;
    public TokenFileRepository(FileStorage storage, Serializer ser, Path basePath) {
        this.storage = storage; this.ser = ser; this.file = basePath.resolve("tokens.json");
    }

    private static class Box { public List<TokenRecuperacaoSenha> tokens = new ArrayList<>(); }
    private Box load(){ byte[] b = storage.read(file); if (b.length==0) return new Box(); return ser.deserialize(b, Box.class); }
    private void save(Box box){ storage.writeAtomic(file, ser.serialize(box)); }

    @Override public Optional<TokenRecuperacaoSenha> find(String token) {
        return load().tokens.stream().filter(t -> t.getToken().equals(token)).findFirst();
    }
    @Override public void save(TokenRecuperacaoSenha token) {
        Box box = load();
        box.tokens.removeIf(t -> t.getToken().equals(token.getToken()));
        box.tokens.add(token);
        save(box);
    }
    @Override public void invalidate(String token) {
        Box box = load();
        box.tokens.removeIf(t -> t.getToken().equals(token));
        save(box);
    }
}
