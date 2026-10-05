package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TankTest {
    private Field field;
    private Tank tank;

    @Before
    public void setUp() {
        field = new Field();
        tank = new Tank(new GridPoint2(1, 1), field);
    }

    @Test
    public void movesOneTileInEachDirection() {
        for (Direction direction : Direction.values()) {
            Tank player = new Tank(new GridPoint2(3, 3), field);
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
        field.addTree(new Tree(new GridPoint2(1, 2)));
        tank.move(Direction.UP);
        tank.update(0.4f);
        assertEquals(new GridPoint2(1, 1), tank.getCoordinates());
        assertEquals(Direction.UP, tank.getDirection());
        assertFalse(tank.isMoving());
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
        Tank player = new Tank(start, field);
        start.set(8, 8);
        player.getCoordinates().set(9, 9);
        assertEquals(new GridPoint2(1, 1), player.getCoordinates());
    }

    @Test
    public void destinationAndProgressDescribeTheCurrentMove() {
        tank.move(Direction.UP);
        assertEquals(new GridPoint2(1, 2), tank.getDestinationCoordinates());
        assertEquals(0f, tank.getMovementProgress(), 0f);
        tank.update(0.1f);
        assertEquals(0.25f, tank.getMovementProgress(), 0.0001f);
        tank.getDestinationCoordinates().set(9, 9);
        tank.update(0.3f);
        assertEquals(new GridPoint2(1, 2), tank.getCoordinates());
        assertEquals(1f, tank.getMovementProgress(), 0f);
    }

    @Test
    public void idleUpdateDoesNotChangePosition() {
        tank.update(1f);
        assertEquals(new GridPoint2(1, 1), tank.getCoordinates());
        assertEquals(tank.getCoordinates(), tank.getDestinationCoordinates());
        assertEquals(1f, tank.getMovementProgress(), 0f);
    }

    @Test
    public void zeroDeltaDoesNotAdvanceMovement() {
        tank.move(Direction.LEFT);
        tank.update(0f);
        assertEquals(new GridPoint2(1, 1), tank.getCoordinates());
        assertEquals(0f, tank.getMovementProgress(), 0f);
        assertTrue(tank.isMoving());
    }

    @Test
    public void canStartAnotherMoveAfterFinishingThePreviousOne() {
        tank.move(Direction.UP);
        tank.update(0.4f);
        tank.move(Direction.RIGHT);
        tank.update(0.4f);
        assertEquals(new GridPoint2(2, 2), tank.getCoordinates());
    }
}
