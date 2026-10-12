package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.model.Tree;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class RandomLevelLoaderTest {
    @Test
    public void generatesTheRequestedNumberOfDistinctTreesAndAFreePlayerStart() {
        for (int seed = 0; seed < 20; seed++) {
            Level level = new RandomLevelLoader(7, 5, 25, new Random(seed)).load();
            Set<GridPoint2> occupied = new HashSet<>();
            assertEquals(7, level.getWidth());
            assertEquals(5, level.getHeight());
            assertEquals(25, level.getTrees().size());
            for (Tree tree : level.getTrees()) {
                GridPoint2 tile = tree.getCoordinates();
                assertTrue(tile.x >= 0 && tile.x < 7 && tile.y >= 0 && tile.y < 5);
                assertTrue(occupied.add(tile));
            }
            GridPoint2 start = level.getPlayerStart();
            assertTrue(start.x >= 0 && start.x < 7 && start.y >= 0 && start.y < 5);
            assertTrue(level.isPassable(start));
            assertFalse(occupied.contains(start));
        }
    }

    @Test
    public void canGenerateATreeFreeSingleCellLevel() {
        Level level = new RandomLevelLoader(1, 1, 0, new Random(1)).load();
        assertEquals(new GridPoint2(), level.getPlayerStart());
        assertTrue(level.getTrees().isEmpty());
    }

    @Test
    public void reservesTheOnlyFreeTileOnAnAlmostFullBoard() {
        Level level = new RandomLevelLoader(3, 2, 5, new Random(8)).load();
        int free = 0;
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                if (level.isPassable(new GridPoint2(x, y))) {
                    free++;
                    assertEquals(level.getPlayerStart(), new GridPoint2(x, y));
                }
            }
        }
        assertEquals(1, free);
    }

    @Test
    public void theSameSeedReproducesTheSameLevel() {
        Level first = new RandomLevelLoader(10, 8, 16, new Random(42)).load();
        Level second = new RandomLevelLoader(10, 8, 16, new Random(42)).load();
        assertEquals(first.getPlayerStart(), second.getPlayerStart());
        assertEquals(positions(first), positions(second));
    }

    @Test
    public void randomizesThePlayerPositionAsWellAsTheObstacles() {
        RandomLevelLoader loader = new RandomLevelLoader(10, 8, 16, new Random(42));
        Set<GridPoint2> starts = new HashSet<>();
        Set<List<GridPoint2>> layouts = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            Level level = loader.load();
            starts.add(level.getPlayerStart());
            layouts.add(positions(level));
        }
        assertTrue(starts.size() > 1);
        assertTrue(layouts.size() > 1);
    }

    @Test
    public void rejectsImpossibleDimensionsAndTreeCounts() {
        for (int[] values : new int[][]{{0, 1, 0}, {1, -1, 0}, {2, 2, -1}, {2, 2, 4},
                {2, 2, 5}, {Integer.MAX_VALUE, 2, 0}}) {
            try {
                new RandomLevelLoader(values[0], values[1], values[2], new Random(1));
                fail("Invalid generation parameters accepted");
            } catch (IllegalArgumentException expected) {
                assertFalse(expected.getMessage().isEmpty());
            }
        }
    }

    private static List<GridPoint2> positions(Level level) {
        return level.getTrees().stream().map(Tree::getCoordinates).collect(Collectors.toList());
    }
}
