package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameModelTest {
    @Test
    public void initializesTheOriginalTankAndTreePositions() {
        GameModel model = new GameModel();
        assertEquals(new GridPoint2(1, 1), model.getTank().getCoordinates());
        assertEquals(Direction.RIGHT, model.getTank().getDirection());
        assertEquals(1, model.getField().getTrees().size());
        assertFalse(model.getField().isPassable(new GridPoint2(1, 3)));
    }

    @Test
    public void updatesMovementAndUsesItsOwnFieldForCollisions() {
        GameModel model = new GameModel();
        model.getTank().move(Direction.UP);
        model.update(0.4f);
        assertEquals(new GridPoint2(1, 2), model.getTank().getCoordinates());
        model.getTank().move(Direction.UP);
        model.update(0.4f);
        assertEquals(new GridPoint2(1, 2), model.getTank().getCoordinates());
        assertFalse(model.getTank().isMoving());
    }

    @Test
    public void newlyAddedTreeImmediatelyBlocksTankMovement() {
        GameModel model = new GameModel();
        model.getField().addTree(new Tree(new GridPoint2(2, 1)));
        model.getTank().move(Direction.RIGHT);
        model.update(0.4f);
        assertEquals(new GridPoint2(1, 1), model.getTank().getCoordinates());
    }
}
