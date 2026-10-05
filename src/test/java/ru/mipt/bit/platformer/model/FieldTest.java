package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class FieldTest {
    @Test
    public void emptyFieldHasNoObstacles() {
        Field field = new Field();
        assertTrue(field.getObstacles().isEmpty());
        assertTrue(field.isPassable(new GridPoint2(1, 1)));
    }

    @Test
    public void allAddedObstaclesBlockTheirTiles() {
        Field field = new Field();
        field.addObstacle(new Tree(new GridPoint2(1, 2)));
        field.addObstacle(new Tree(new GridPoint2(2, 1)));
        assertFalse(field.isPassable(new GridPoint2(1, 2)));
        assertFalse(field.isPassable(new GridPoint2(2, 1)));
        assertTrue(field.isPassable(new GridPoint2(0, 1)));
        assertEquals(2, field.getObstacles().size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void obstaclesCannotBeRemovedThroughTheGetter() {
        Field field = new Field();
        field.addObstacle(new Tree(new GridPoint2(1, 2)));
        field.getObstacles().clear();
    }

    @Test
    public void obstacleListReflectsNewObstacles() {
        Field field = new Field();
        List<Obstacle> trees = field.getObstacles();
        Tree tree = new Tree(new GridPoint2(1, 2));
        field.addObstacle(tree);
        assertSame(tree, trees.get(0));
        assertFalse(field.isPassable(tree.getCoordinates()));
    }

    @Test
    public void acceptsANewObstacleWithADifferentFootprint() {
        Field field = new Field();
        field.addObstacle(tile -> tile.x >= 2 && tile.x <= 4 && tile.y == 3);
        assertFalse(field.isPassable(new GridPoint2(2, 3)));
        assertFalse(field.isPassable(new GridPoint2(4, 3)));
        assertTrue(field.isPassable(new GridPoint2(5, 3)));
        assertTrue(field.isPassable(new GridPoint2(2, 4)));
    }

    @Test(expected = NullPointerException.class)
    public void rejectsNullObstacles() {
        new Field().addObstacle(null);
    }
}
