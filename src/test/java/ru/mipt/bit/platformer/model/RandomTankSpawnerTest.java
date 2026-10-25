package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.level.Level;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class RandomTankSpawnerTest {
    @Test
    public void spawnsRequestedNumberWithoutOverlappingPlayerOrTrees() {
        Level level = new Level(8, 6, new GridPoint2(3, 2), Arrays.asList(
                new Tree(new GridPoint2(0, 0)), new Tree(new GridPoint2(3, 3))));
        Set<List<GridPoint2>> layouts = new HashSet<>();
        for (int seed = 0; seed < 20; seed++) {
            TankField field = new TankField(level);
            Tank player = field.createTank(level.getPlayerStart(), 0.4f, Direction.RIGHT);
            List<Tank> bots = new RandomTankSpawner(8, 6, 10, 0.8f, new Random(seed)).spawn(field);
            assertEquals(10, bots.size());
            assertEquals(11, field.getTanks().size());
            Set<GridPoint2> occupied = new HashSet<>();
            occupied.add(player.getCoordinates());
            for (Tank bot : bots) {
                assertTrue(level.isPassable(bot.getCoordinates()));
                assertTrue(occupied.add(bot.getCoordinates()));
                assertFalse(field.isPassable(bot.getCoordinates()));
                assertFalse(bot.isMoving());
            }
            layouts.add(positions(bots));
        }
        assertTrue(layouts.size() > 1);
    }

    @Test
    public void usesOnlyAvailableSpaceOnSmallAndFullLevels() {
        TankField field = new TankField(new Level(2, 1, new GridPoint2(0, 0), Collections.emptyList()));
        field.createTank(new GridPoint2(0, 0), 0.4f, Direction.RIGHT);
        RandomTankSpawner spawner = new RandomTankSpawner(2, 1, 5, 0.4f, new Random(0));
        assertEquals(Collections.singletonList(new GridPoint2(1, 0)), positions(spawner.spawn(field)));
        assertTrue(spawner.spawn(field).isEmpty());
        assertEquals(2, field.getTanks().size());
    }

    @Test
    public void doesNotSpawnOnEitherCellOfAMovingTank() {
        TankField field = new TankField(new Level(3, 1, new GridPoint2(0, 0), Collections.emptyList()));
        Tank player = field.createTank(new GridPoint2(0, 0), 0.4f, Direction.RIGHT);
        player.move(Direction.RIGHT);
        List<Tank> bots = new RandomTankSpawner(3, 1, 3, 0.4f, new Random(0)).spawn(field);
        assertEquals(Collections.singletonList(new GridPoint2(2, 0)), positions(bots));
        assertEquals(2, field.getTanks().size());
    }

    @Test
    public void seedReproducesPositionsAndDirections() {
        Level level = new Level(5, 5, new GridPoint2(0, 0), Collections.emptyList());
        List<Tank> first = new RandomTankSpawner(5, 5, 5, 0.4f, new Random(42)).spawn(new TankField(level));
        List<Tank> second = new RandomTankSpawner(5, 5, 5, 0.4f, new Random(42)).spawn(new TankField(level));
        assertEquals(positions(first), positions(second));
        assertEquals(first.stream().map(Tank::getDirection).collect(Collectors.toList()),
                second.stream().map(Tank::getDirection).collect(Collectors.toList()));
    }

    @Test
    public void appliesMovementDurationAndRegistersCollisionsBetweenSpawnedTanks() {
        TankField field = new TankField(tile -> true);
        List<Tank> bots = new RandomTankSpawner(2, 1, 2, 0.8f, new Random(1)).spawn(field);
        for (Tank bot : bots) {
            bot.move(bot.getCoordinates().x == 0 ? Direction.RIGHT : Direction.LEFT);
            assertFalse(bot.isMoving());
            bot.move(Direction.UP);
            bot.update(0.4f);
            assertEquals(0.5f, bot.getMovementProgress(), 0f);
        }
        assertThrows(UnsupportedOperationException.class, bots::clear);
    }

    @Test
    public void zeroCountDoesNotAddTanks() {
        TankField field = new TankField(tile -> true);
        assertTrue(new RandomTankSpawner(2, 2, 0, 0.4f, new Random()).spawn(field).isEmpty());
        assertTrue(field.getTanks().isEmpty());
    }

    @Test
    public void rejectsInvalidConfiguration() {
        for (int[] size : new int[][]{{0, 1}, {1, 0}, {-1, 2}, {Integer.MAX_VALUE, 2}}) {
            assertThrows(IllegalArgumentException.class,
                    () -> new RandomTankSpawner(size[0], size[1], 1, 0.4f, new Random()));
        }
        assertThrows(IllegalArgumentException.class, () -> new RandomTankSpawner(2, 2, -1, 0.4f, new Random()));
        for (float duration : new float[]{0f, -1f, Float.NaN, Float.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class,
                    () -> new RandomTankSpawner(2, 2, 1, duration, new Random()));
        }
        assertThrows(NullPointerException.class, () -> new RandomTankSpawner(2, 2, 1, 0.4f, null));
        assertThrows(NullPointerException.class,
                () -> new RandomTankSpawner(2, 2, 1, 0.4f, new Random()).spawn(null));
    }

    private static List<GridPoint2> positions(List<Tank> tanks) {
        return tanks.stream().map(Tank::getCoordinates).collect(Collectors.toList());
    }
}
