package ru.mipt.bit.platformer;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import org.junit.Before;
import org.junit.Test;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import static org.junit.Assert.*;

public class TankTest {
    private Field field;
    private Tank tank;

    @Before
    public void setUp() {
        field = new Field(new TiledMapTileLayer(10, 8, 128, 128));
        tank = new Tank(new TextureRegion(), new GridPoint2(1, 1), field);
    }

    @Test
    public void movesOneTileInEachDirection() {
        for (Direction direction : Direction.values()) {
            Tank player = new Tank(new TextureRegion(), new GridPoint2(3, 3), field);
            player.move(direction);
            player.update(0.4f);
            assertEquals(direction.nextTile(new GridPoint2(3, 3)), player.getCoordinates());
            assertEquals(direction, player.getDirection());
            assertFalse(player.isMoving());
        }
    }

    @Test
    public void completesMovementOnlyAfterItsDuration() {
        tank.move(Direction.UP);
        tank.update(0.2f);
        assertEquals(new GridPoint2(1, 1), tank.getCoordinates());
        assertTrue(tank.isMoving());
        tank.update(0.2f);
        assertEquals(new GridPoint2(1, 2), tank.getCoordinates());
        assertFalse(tank.isMoving());
    }

    @Test
    public void cannotChangeDirectionDuringMovement() {
        tank.move(Direction.UP);
        tank.update(0.2f);
        tank.move(Direction.RIGHT);
        tank.update(0.2f);
        assertEquals(new GridPoint2(1, 2), tank.getCoordinates());
        assertEquals(Direction.UP, tank.getDirection());
    }

    @Test
    public void turnsTowardsTreeWithoutMovingIntoIt() {
        field.addTree(new Tree(new TextureRegion(), new GridPoint2(1, 2)));
        tank.move(Direction.UP);
        tank.update(0.4f);
        assertEquals(new GridPoint2(1, 1), tank.getCoordinates());
        assertEquals(Direction.UP, tank.getDirection());
        assertFalse(tank.isMoving());
    }

    @Test
    public void fieldChecksAllTrees() {
        field.addTree(new Tree(new TextureRegion(), new GridPoint2(1, 2)));
        field.addTree(new Tree(new TextureRegion(), new GridPoint2(2, 1)));
        assertFalse(field.isPassable(new GridPoint2(1, 2)));
        assertFalse(field.isPassable(new GridPoint2(2, 1)));
        assertTrue(field.isPassable(new GridPoint2(0, 1)));
    }

    @Test
    public void largeFrameDoesNotOvershootDestination() {
        tank.move(Direction.RIGHT);
        tank.update(2f);
        assertEquals(new GridPoint2(2, 1), tank.getCoordinates());
        assertFalse(tank.isMoving());
    }

    @Test
    public void coordinatesCannotBeChangedFromOutside() {
        GridPoint2 start = new GridPoint2(1, 1);
        Tank player = new Tank(new TextureRegion(), start, field);
        start.set(8, 8);
        player.getCoordinates().set(9, 9);
        assertEquals(new GridPoint2(1, 1), player.getCoordinates());
    }

    @Test
    public void directionDoesNotModifyStartingCoordinates() {
        GridPoint2 start = new GridPoint2(3, 3);
        assertEquals(new GridPoint2(3, 4), Direction.UP.nextTile(start));
        assertEquals(new GridPoint2(2, 3), Direction.LEFT.nextTile(start));
        assertEquals(new GridPoint2(3, 2), Direction.DOWN.nextTile(start));
        assertEquals(new GridPoint2(4, 3), Direction.RIGHT.nextTile(start));
        assertEquals(new GridPoint2(3, 3), start);
    }
}
