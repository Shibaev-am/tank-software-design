package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class FileLevelLoaderTest {
    @Test
    public void readsTheWholeBoardWithTheFirstRowAtTheTop() throws IOException {
        Level level = loader("T__T\n_X__\n__T_\n").load();
        assertEquals(4, level.getWidth());
        assertEquals(3, level.getHeight());
        assertEquals(new GridPoint2(1, 1), level.getPlayerStart());
        assertEquals(3, level.getTrees().size());
        assertFalse(level.isPassable(new GridPoint2(0, 2)));
        assertFalse(level.isPassable(new GridPoint2(3, 2)));
        assertFalse(level.isPassable(new GridPoint2(2, 0)));
        assertTrue(level.isPassable(new GridPoint2(0, 0)));
    }

    @Test
    public void acceptsWindowsNewlinesAndNoFinalNewline() throws IOException {
        Level level = loader("T_\r\n_X").load();
        assertEquals(new GridPoint2(1, 0), level.getPlayerStart());
        assertEquals(new GridPoint2(0, 1), level.getTrees().get(0).getCoordinates());
    }

    @Test
    public void closesTheFileAfterLoading() throws IOException {
        boolean[] closed = {false};
        ByteArrayInputStream stream = new ByteArrayInputStream("X".getBytes(StandardCharsets.UTF_8)) {
            @Override
            public void close() {
                closed[0] = true;
            }
        };
        new FileLevelLoader(() -> stream).load();
        assertTrue(closed[0]);
    }

    @Test
    public void reportsInvalidLevels() throws IOException {
        for (String text : new String[]{"", "\n", "___", "XX", "X_\n_", "X_\n\n__", "X?", "X "}) {
            try {
                loader(text).load();
                fail("Invalid level accepted: " + text);
            } catch (IllegalArgumentException expected) {
                assertFalse(expected.getMessage().isEmpty());
            }
        }
    }

    @Test
    public void unknownCellsAreReportedWithLineAndColumn() throws IOException {
        try {
            loader("X_\n_?").load();
            fail("Unknown cell accepted");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("line 2, column 2"));
        }
    }

    @Test(expected = IOException.class)
    public void reportsAMissingFile() throws IOException {
        new FileLevelLoader(() -> null).load();
    }

    @Test
    public void propagatesFileReadErrors() {
        IOException failure = new IOException("Cannot open file");
        try {
            new FileLevelLoader(() -> { throw failure; }).load();
            fail("Read error ignored");
        } catch (IOException actual) {
            assertSame(failure, actual);
        }
    }

    private static FileLevelLoader loader(String text) {
        return new FileLevelLoader(() -> new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));
    }
}
