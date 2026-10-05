package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class FieldTest {
    @Test
    public void emptyFieldHasNoObstacles() {
        Field field = new Field();
        assertTrue(field.getTrees().isEmpty());
        assertTrue(field.isPassable(new GridPoint2(1, 1)));
    }

    @Test
    public void allAddedTreesBlockTheirTiles() {
        Field field = new Field();
        field.addTree(new Tree(new GridPoint2(1, 2)));
        field.addTree(new Tree(new GridPoint2(2, 1)));
        assertFalse(field.isPassable(new GridPoint2(1, 2)));
        assertFalse(field.isPassable(new GridPoint2(2, 1)));
        assertTrue(field.isPassable(new GridPoint2(0, 1)));
        assertEquals(2, field.getTrees().size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void treesCannotBeRemovedThroughTheGetter() {
        Field field = new Field();
        field.addTree(new Tree(new GridPoint2(1, 2)));
        field.getTrees().clear();
    }

    @Test
    public void treeViewReflectsNewObstacles() {
        Field field = new Field();
        List<Tree> trees = field.getTrees();
        Tree tree = new Tree(new GridPoint2(1, 2));
        field.addTree(tree);
        assertSame(tree, trees.get(0));
        assertFalse(field.isPassable(tree.getCoordinates()));
    }
}
