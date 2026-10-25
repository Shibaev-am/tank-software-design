package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.level.Level;

import java.util.Collections;

import static org.junit.Assert.*;

public class TankFieldTest {
    private final TankField field = new TankField(new Level(5, 4, new GridPoint2(0, 0),
            Collections.singletonList(new Tree(new GridPoint2(4, 3)))));

    @Test
    public void reservesBothEndpointsUntilMovementFinishes() {
        Tank leader = tankAt(1, 1);
        Tank follower = tankAt(0, 1);
        Tank contender = tankAt(2, 2);
        leader.move(Direction.RIGHT);
        assertFalse(field.isPassable(new GridPoint2(1, 1)));
        assertFalse(field.isPassable(new GridPoint2(2, 1)));
        follower.move(Direction.RIGHT);
        contender.move(Direction.DOWN);
        assertFalse(follower.isMoving());
        assertFalse(contender.isMoving());
        leader.update(0.2f);
        follower.move(Direction.RIGHT);
        contender.move(Direction.DOWN);
        assertFalse(follower.isMoving());
        assertFalse(contender.isMoving());
        leader.update(0.2f);
        assertTrue(field.isPassable(new GridPoint2(1, 1)));
        assertFalse(field.isPassable(new GridPoint2(2, 1)));
        follower.move(Direction.RIGHT);
        contender.move(Direction.DOWN);
        assertTrue(follower.isMoving());
        assertFalse(contender.isMoving());
    }

    @Test
    public void onlyOneTankCanReserveAFreeDestination() {
        Tank left = tankAt(0, 1);
        Tank right = tankAt(2, 1);
        left.move(Direction.RIGHT);
        right.move(Direction.LEFT);
        assertTrue(left.isMoving());
        assertFalse(right.isMoving());
        assertEquals(Direction.LEFT, right.getDirection());
        assertTrue(field.isPassable(new GridPoint2(2, 2)));
    }

    @Test
    public void adjacentTanksCannotSwapPlaces() {
        Tank left = tankAt(0, 1);
        Tank right = tankAt(1, 1);
        left.move(Direction.RIGHT);
        right.move(Direction.LEFT);
        assertFalse(left.isMoving());
        assertFalse(right.isMoving());
        assertEquals(new GridPoint2(0, 1), left.getCoordinates());
        assertEquals(new GridPoint2(1, 1), right.getCoordinates());
    }

    @Test
    public void respectsTreesAndEveryLevelBoundary() {
        assertBlocked(0, 0, Direction.LEFT);
        assertBlocked(0, 0, Direction.DOWN);
        assertBlocked(4, 0, Direction.RIGHT);
        assertBlocked(0, 3, Direction.UP);
        assertBlocked(3, 3, Direction.RIGHT);
    }

    @Test
    public void refusesToSpawnOnTreesOutsideOrOnEitherEndOfAMove() {
        Tank tank = tankAt(1, 1);
        tank.move(Direction.RIGHT);
        for (GridPoint2 tile : new GridPoint2[]{new GridPoint2(4, 3), new GridPoint2(-1, 0),
                new GridPoint2(5, 0), new GridPoint2(0, 4), new GridPoint2(1, 1), new GridPoint2(2, 1)}) {
            assertThrows(IllegalArgumentException.class,
                    () -> field.createTank(tile, 0.4f, Direction.RIGHT));
        }
        assertEquals(1, field.getTanks().size());
    }

    @Test
    public void invalidTankDoesNotOccupySpaceAndTankListCannotBeModified() {
        GridPoint2 tile = new GridPoint2(1, 1);
        assertThrows(IllegalArgumentException.class, () -> field.createTank(tile, 0f, Direction.RIGHT));
        assertThrows(NullPointerException.class, () -> field.createTank(tile, 0.4f, null));
        assertTrue(field.isPassable(tile));
        Tank tank = tankAt(1, 1);
        assertSame(tank, field.getTanks().get(0));
        assertThrows(UnsupportedOperationException.class, () -> field.getTanks().clear());
        assertThrows(NullPointerException.class, () -> new TankField(null));
        assertThrows(NullPointerException.class, () -> field.createTank(null, 0.4f, Direction.RIGHT));
    }

    @Test
    public void tankOccupancyTracksMovesInEveryDirection() {
        for (Direction direction : Direction.values()) {
            Tank tank = new Tank(new GridPoint2(1, 1), tile -> true, 0.4f, Direction.UP);
            GridPoint2 start = tank.getCoordinates();
            GridPoint2 end = direction.nextTile(start);
            assertTrue(tank.occupies(start));
            assertFalse(tank.occupies(end));
            tank.move(direction);
            assertTrue(tank.occupies(start));
            assertTrue(tank.occupies(end));
            assertFalse(tank.occupies(new GridPoint2(3, 3)));
            tank.update(0.4f);
            assertFalse(tank.occupies(start));
            assertTrue(tank.occupies(end));
        }
    }

    private Tank tankAt(int x, int y) {
        return field.createTank(new GridPoint2(x, y), 0.4f, Direction.RIGHT);
    }

    private void assertBlocked(int x, int y, Direction direction) {
        TankField separate = new TankField(new Level(5, 4, new GridPoint2(0, 0),
                Collections.singletonList(new Tree(new GridPoint2(4, 3)))));
        Tank tank = separate.createTank(new GridPoint2(x, y), 0.4f, Direction.UP);
        tank.move(direction);
        tank.update(0.4f);
        assertFalse(tank.isMoving());
        assertEquals(new GridPoint2(x, y), tank.getCoordinates());
        assertEquals(direction, tank.getDirection());
    }
}
