package ir.smmh.storage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PermanentStorageImpl implements PermanentStorage {

    private final String root;

    public PermanentStorageImpl(@NotNull final String root) {
        this.root = root;
    }

    @Override
    public boolean exists(@NotNull String filename) {
        return Files.exists(Path.of(root, filename));
    }

    @Nullable
    @Override
    public String read(@NotNull String filename) {
        try {
            return Files.readString(Path.of(root, filename));
        } catch (IOException e) {
            System.err.println("FAILED TO READ FILE: " + filename);
            return null;
        }
    }

    @Override
    public boolean write(@NotNull String filename, @NotNull String contents) {
        try {
            Files.writeString(Path.of(root, filename), contents);
            return true;
        } catch (IOException e) {
            System.err.println("FAILED TO WRITE FILE: " + filename);
            return false;
        }
    }

    @Override
    public boolean delete(@NotNull String filename) {
        try {
            Files.delete(Path.of(root, filename));
            return true;
        } catch (IOException e) {
            System.err.println("FAILED TO DELETE FILE: " + filename);
            return false;
        }
    }
}
