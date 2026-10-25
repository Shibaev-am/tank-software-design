package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import static org.junit.Assert.*;

public class DirectionTest {
    @Test
    public void directionsStoreMatchingGridVectorsAndSpriteRotations() {
        Direction[] directions = {Direction.UP, Direction.LEFT, Direction.DOWN, Direction.RIGHT};
        GridPoint2[] vectors = {new GridPoint2(0, 1), new GridPoint2(-1, 0),
                new GridPoint2(0, -1), new GridPoint2(1, 0)};
        float[] rotations = {90f, -180f, -90f, 0f};
        for (int i = 0; i < directions.length; i++) {
            assertEquals(vectors[i], directions[i].getVector());
            assertEquals(rotations[i], directions[i].getRotation(), 0f);
        }
    }

    @Test
    public void nextTileDoesNotModifyTheStartingPosition() {
        GridPoint2 start = new GridPoint2(3, 3);
        assertEquals(new GridPoint2(3, 4), Direction.UP.nextTile(start));
        assertEquals(new GridPoint2(2, 3), Direction.LEFT.nextTile(start));
        assertEquals(new GridPoint2(3, 2), Direction.DOWN.nextTile(start));
        assertEquals(new GridPoint2(4, 3), Direction.RIGHT.nextTile(start));
        assertEquals(new GridPoint2(3, 3), start);
    }

    @Test
    public void vectorCannotBeChangedFromOutsideTheEnum() {
        Direction.UP.getVector().set(10, 10);
        assertEquals(new GridPoint2(0, 1), Direction.UP.getVector());
        assertEquals(new GridPoint2(2, 3), Direction.UP.nextTile(new GridPoint2(2, 2)));
    }
}
