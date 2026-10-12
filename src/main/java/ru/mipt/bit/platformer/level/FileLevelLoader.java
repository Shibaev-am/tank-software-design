package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.Tree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileLevelLoader implements LevelLoader {
    private final InputStreamSource source;

    public FileLevelLoader(InputStreamSource source) {
        this.source = Objects.requireNonNull(source);
    }

    @Override
    public Level load() throws IOException {
        InputStream stream = source.open();
        if (stream == null) {
            throw new IOException("Level file was not found");
        }
        List<String> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String row;
            while ((row = reader.readLine()) != null) {
                rows.add(row);
            }
        }
        if (rows.isEmpty() || rows.get(0).isEmpty()) {
            throw new IllegalArgumentException("Level must not be empty");
        }
        int width = rows.get(0).length();
        List<Tree> trees = new ArrayList<>();
        GridPoint2 start = null;
        for (int row = 0; row < rows.size(); row++) {
            String cells = rows.get(row);
            if (cells.length() != width) {
                throw new IllegalArgumentException("Different row width at line " + (row + 1));
            }
            for (int column = 0; column < width; column++) {
                GridPoint2 tile = new GridPoint2(column, rows.size() - row - 1);
                switch (cells.charAt(column)) {
                    case 'T':
                        trees.add(new Tree(tile));
                        break;
                    case 'X':
                        if (start != null) {
                            throw new IllegalArgumentException("Level must contain exactly one X");
                        }
                        start = tile;
                        break;
                    case '_':
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown cell at line " + (row + 1)
                                + ", column " + (column + 1) + ": " + cells.charAt(column));
                }
            }
        }
        if (start == null) {
            throw new IllegalArgumentException("Level must contain exactly one X");
        }
        return new Level(width, rows.size(), start, trees);
    }

    @FunctionalInterface
    public interface InputStreamSource {
        InputStream open() throws IOException;
    }
}
