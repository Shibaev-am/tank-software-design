package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.model.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class LevelTest {
    @Test
    public void treesAndBordersBlockMovementWhilePlayerStartsOnAFreeTile() {
        Level level = new Level(3, 2, new GridPoint2(0, 0),
                Collections.singletonList(new Tree(new GridPoint2(1, 1))));
        assertEquals(3, level.getWidth());
        assertEquals(2, level.getHeight());
        assertTrue(level.isPassable(level.getPlayerStart()));
        assertFalse(level.isPassable(new GridPoint2(1, 1)));
        assertTrue(level.isPassable(new GridPoint2(2, 1)));
        for (GridPoint2 outside : Arrays.asList(new GridPoint2(-1, 0), new GridPoint2(0, -1),
                new GridPoint2(3, 0), new GridPoint2(0, 2))) {
            assertFalse(level.isPassable(outside));
        }
    }

    @Test
    public void ownsItsStartPositionAndObstacleCollection() {
        GridPoint2 start = new GridPoint2(0, 0);
        List<Tree> trees = new ArrayList<>();
        trees.add(new Tree(new GridPoint2(1, 1)));
        Level level = new Level(2, 2, start, trees);
        start.set(1, 1);
        level.getPlayerStart().set(1, 1);
        trees.clear();
        level.getTrees().get(0).getCoordinates().set(0, 0);
        assertEquals(new GridPoint2(0, 0), level.getPlayerStart());
        assertFalse(level.isPassable(new GridPoint2(1, 1)));
        assertTrue(level.isPassable(new GridPoint2(0, 0)));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void cannotChangeTreesThroughTheGetter() {
        new Level(1, 1, new GridPoint2(), Collections.emptyList()).getTrees().clear();
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsAnEmptyBoard() {
        new Level(0, 2, new GridPoint2(), Collections.emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsAnOutOfBoundsPlayer() {
        new Level(2, 2, new GridPoint2(2, 0), Collections.emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsAnOutOfBoundsTree() {
        new Level(2, 2, new GridPoint2(),
                Collections.singletonList(new Tree(new GridPoint2(0, 2))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsATreeOnThePlayerStart() {
        new Level(1, 1, new GridPoint2(), Collections.singletonList(new Tree(new GridPoint2())));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsDuplicateTrees() {
        new Level(2, 2, new GridPoint2(), Arrays.asList(
                new Tree(new GridPoint2(1, 1)), new Tree(new GridPoint2(1, 1))));
    }
}
