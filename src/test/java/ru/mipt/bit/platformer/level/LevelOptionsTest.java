package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class LevelOptionsTest {
    @Rule
    public TemporaryFolder directory = new TemporaryFolder();

    @Test
    public void loadsTheBundledFileWithoutArguments() throws IOException {
        Level level = LevelOptions.fromArguments(new String[0]).load();
        assertEquals(10, level.getWidth());
        assertEquals(8, level.getHeight());
        assertEquals(new GridPoint2(4, 2), level.getPlayerStart());
        assertEquals(15, level.getTrees().size());
    }

    @Test
    public void loadsAnExternalFileIncludingAPathWithSpaces() throws IOException {
        Path file = directory.newFile("my level.txt").toPath();
        Files.writeString(file, "_T\nX_", StandardCharsets.UTF_8);
        Level level = LevelOptions.fromArguments(new String[]{"--level", file.toString()}).load();
        assertEquals(new GridPoint2(0, 0), level.getPlayerStart());
        assertEquals(new GridPoint2(1, 1), level.getTrees().get(0).getCoordinates());
    }

    @Test
    public void selectsRandomGenerationWithAnOptionalSeed() throws IOException {
        Level level = LevelOptions.fromArguments(new String[]{"--random"}).load();
        assertEquals(16, level.getTrees().size());
        assertTrue(level.isPassable(level.getPlayerStart()));
        Level first = LevelOptions.fromArguments(new String[]{"--random", "42"}).load();
        Level second = LevelOptions.fromArguments(new String[]{"--random", "42"}).load();
        assertEquals(first.getPlayerStart(), second.getPlayerStart());
        for (int i = 0; i < 16; i++) {
            assertEquals(first.getTrees().get(i).getCoordinates(), second.getTrees().get(i).getCoordinates());
        }
    }

    @Test
    public void rejectsUnknownOrIncompleteArguments() {
        for (String[] args : new String[][]{{"--unknown"}, {"--level"}, {"--random", "bad-seed"},
                {"--level", "file", "extra"}, {"--random", "1", "extra"}}) {
            try {
                LevelOptions.fromArguments(args);
                fail("Invalid arguments accepted");
            } catch (IllegalArgumentException expected) {
                assertFalse(expected.getMessage().isEmpty());
            }
        }
    }

    @Test(expected = IOException.class)
    public void reportsMissingExternalFiles() throws IOException {
        LevelOptions.fromArguments(new String[]{"--level", directory.getRoot().toPath()
                .resolve("missing.txt").toString()}).load();
    }
}
