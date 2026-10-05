package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import static org.junit.Assert.*;

public class TreeTest {
    @Test
    public void occupiesItsOwnTileAndNotAdjacentTiles() {
        Tree tree = new Tree(new GridPoint2(1, 3));
        assertTrue(tree.occupies(new GridPoint2(1, 3)));
        for (Direction direction : Direction.values()) {
            assertFalse(tree.occupies(direction.nextTile(new GridPoint2(1, 3))));
        }
    }

    @Test
    public void externalCoordinateChangesDoNotMoveTheObstacle() {
        GridPoint2 start = new GridPoint2(1, 3);
        Tree tree = new Tree(start);
        start.set(0, 0);
        tree.getCoordinates().set(0, 0);
        assertTrue(tree.occupies(new GridPoint2(1, 3)));
        assertFalse(tree.occupies(new GridPoint2(0, 0)));
    }
}
