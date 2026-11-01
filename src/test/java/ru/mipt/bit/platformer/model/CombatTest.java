package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import ru.mipt.bit.platformer.level.Level;

import static org.junit.Assert.*;

public class CombatTest {
    @Test
    public void createsBulletOneTileAheadInEveryDirection() {
        for (Direction direction : Direction.values()) {
            TankField field = field(25, List.of());
            Tank tank = field.createTank(new GridPoint2(3, 3), 0.4f, direction);
            tank.shoot();
            Bullet bullet = bullets(field).get(0);
            assertEquals(direction.nextTile(tank.getCoordinates()), bullet.getCoordinates());
            assertEquals(direction, bullet.getDirection());
            assertSame(tank, bullet.getOwner());
            assertEquals(25, bullet.getDamage());
        }
    }

    @Test
    public void adjacentTargetIsHitImmediatelyWithoutAFrameDelay() {
        TankField field = field(35, List.of());
        Tank shooter = tank(field, 0, 1, Direction.RIGHT);
        Tank target = tank(field, 1, 1, Direction.LEFT);
        shooter.shoot();
        assertEquals(65, target.getHealth());
        assertEquals(100, shooter.getHealth());
        assertTrue(bullets(field).isEmpty());
    }

    @Test
    public void zeroHealthRemovesTankAndFreesBothMovementCells() {
        TankField field = field(100, List.of());
        Tank shooter = tank(field, 0, 1, Direction.RIGHT);
        Tank target = tank(field, 1, 1, Direction.RIGHT);
        target.move(Direction.RIGHT);
        shooter.shoot();
        assertEquals(0, target.getHealth());
        assertFalse(target.isAlive());
        assertFalse(field.getModel().getObjects().contains(target));
        assertEquals(List.of(shooter), field.getTanks());
        assertTrue(field.isPassable(new GridPoint2(1, 1)));
        assertTrue(field.isPassable(new GridPoint2(2, 1)));
        target.move(Direction.UP);
        target.shoot();
        target.update(10f);
        target.takeDamage(Integer.MAX_VALUE);
        assertTrue(bullets(field).isEmpty());
        assertEquals(new GridPoint2(1, 1), target.getCoordinates());
        assertEquals(0, target.getHealth());
    }

    @Test
    public void canHitTheDestinationReservedByAMovingTank() {
        TankField field = field(25, List.of());
        Tank shooter = tank(field, 0, 1, Direction.RIGHT);
        Tank target = tank(field, 1, 2, Direction.DOWN);
        target.move(Direction.DOWN);
        shooter.shoot();
        assertEquals(75, target.getHealth());
        assertTrue(bullets(field).isEmpty());
    }

    @Test
    public void tankEnteringABulletsCellIsHitBeforeItCanFinishTheMove() {
        TankField field = field(100, List.of());
        Tank shooter = tank(field, 0, 1, Direction.RIGHT);
        Tank target = tank(field, 1, 2, Direction.DOWN);
        shooter.shoot();
        target.move(Direction.DOWN);
        field.getModel().update(1f);
        assertFalse(target.isAlive());
        assertEquals(new GridPoint2(1, 2), target.getCoordinates());
        assertTrue(bullets(field).isEmpty());
    }

    @Test
    public void movingShooterDoesNotHitItsOwnReservedDestination() {
        TankField field = field(100, List.of());
        Tank shooter = tank(field, 0, 1, Direction.RIGHT);
        shooter.move(Direction.RIGHT);
        shooter.shoot();
        field.getModel().update(0.05f);
        assertEquals(100, shooter.getHealth());
        assertEquals(1, bullets(field).size());
    }

    @Test
    public void reloadPreventsMultipleShotsUntilTimePasses() {
        TankField field = field(25, List.of());
        Tank shooter = tank(field, 0, 0, Direction.RIGHT);
        shooter.shoot();
        shooter.shoot();
        assertEquals(1, bullets(field).size());
        field.getModel().update(0.25f);
        shooter.shoot();
        assertEquals(1, bullets(field).size());
        field.getModel().update(0.25f);
        shooter.shoot();
        assertEquals(2, bullets(field).size());
    }

    @Test
    public void bulletFliesLinearlyAndKeepsItsOriginalDirection() {
        TankField field = field(25, List.of());
        Tank shooter = tank(field, 2, 2, Direction.RIGHT);
        shooter.shoot();
        Bullet bullet = bullets(field).get(0);
        shooter.move(Direction.UP);
        bullet.update(0.05f);
        assertEquals(new GridPoint2(3, 2), bullet.getCoordinates());
        assertEquals(new GridPoint2(4, 2), bullet.getDestinationCoordinates());
        assertEquals(0.5f, bullet.getMovementProgress(), 0.0001f);
        bullet.getCoordinates().set(99, 99);
        bullet.getDestinationCoordinates().set(99, 99);
        bullet.update(0.05f);
        assertEquals(new GridPoint2(4, 2), bullet.getCoordinates());
        assertEquals(Direction.RIGHT, bullet.getDirection());
    }

    @Test
    public void largeDeltaCannotSkipATreeOrDamageTheTankBehindIt() {
        TankField field = field(100, List.of(new Tree(new GridPoint2(3, 1))));
        Tank shooter = tank(field, 0, 1, Direction.RIGHT);
        Tank target = tank(field, 5, 1, Direction.LEFT);
        shooter.shoot();
        field.getModel().update(2f);
        assertTrue(bullets(field).isEmpty());
        assertEquals(100, target.getHealth());
    }

    @Test
    public void largeDeltaHitsOnlyTheFirstTankOnThePath() {
        TankField field = field(200, List.of());
        Tank shooter = tank(field, 0, 1, Direction.RIGHT);
        Tank first = tank(field, 3, 1, Direction.LEFT);
        Tank second = tank(field, 5, 1, Direction.LEFT);
        shooter.shoot();
        field.getModel().update(2f);
        assertFalse(first.isAlive());
        assertEquals(100, second.getHealth());
        assertTrue(bullets(field).isEmpty());
    }

    @Test
    public void shotsIntoTreesAndOutsideEveryBoundaryDisappearAtSpawn() {
        for (Direction direction : Direction.values()) {
            Level level = new Level(1, 1, new GridPoint2(0, 0), List.of());
            TankField field = new TankField(level);
            tank(field, 0, 0, direction).shoot();
            assertTrue(bullets(field).isEmpty());
        }
        TankField field = field(25, List.of(new Tree(new GridPoint2(1, 0))));
        tank(field, 0, 0, Direction.RIGHT).shoot();
        assertTrue(bullets(field).isEmpty());
    }

    @Test
    public void shotsLeaveTheLevelAndDestroyIsIdempotent() {
        TankField field = field(25, List.of());
        tank(field, 0, 0, Direction.RIGHT).shoot();
        Bullet bullet = bullets(field).get(0);
        field.getModel().update(2f);
        assertFalse(bullet.isActive());
        assertTrue(bullets(field).isEmpty());
        GridPoint2 last = bullet.getCoordinates();
        bullet.destroy();
        bullet.update(1f);
        field.addBullet(bullet);
        assertEquals(last, bullet.getCoordinates());
        assertTrue(bullets(field).isEmpty());
    }

    @Test
    public void bulletsSpawnedInTheSameCellDestroyEachOther() {
        TankField field = field(100, List.of());
        Tank left = tank(field, 0, 1, Direction.RIGHT);
        Tank right = tank(field, 2, 1, Direction.LEFT);
        left.shoot();
        right.shoot();
        assertTrue(bullets(field).isEmpty());
        assertEquals(100, left.getHealth());
        assertEquals(100, right.getHealth());
    }

    @Test
    public void opposingBulletsCannotPassThroughOrSwapCells() {
        for (int separation : new int[]{3, 4, 6}) {
            for (float delta : new float[]{0.025f, 0.1f, 2f}) {
                TankField field = field(100, List.of());
                Tank left = tank(field, 0, 1, Direction.RIGHT);
                Tank right = tank(field, separation, 1, Direction.LEFT);
                left.shoot();
                right.shoot();
                for (int frame = 0; frame < 20; frame++) {
                    field.getModel().update(delta);
                }
                assertTrue(bullets(field).isEmpty());
                assertEquals(100, left.getHealth());
                assertEquals(100, right.getHealth());
            }
        }
    }

    @Test
    public void perpendicularBulletsCollideEvenWhenTheFrameSpansSeveralTiles() {
        TankField field = field(100, List.of());
        tank(field, 0, 2, Direction.RIGHT).shoot();
        tank(field, 2, 0, Direction.UP).shoot();
        field.getModel().update(0.4f);
        assertTrue(bullets(field).isEmpty());
        assertEquals(2, field.getTanks().size());
    }

    @Test
    public void aTreeAddedToTheModelBlocksBulletsThroughTheCollisionInterface() {
        TankField field = field(100, List.of());
        Tree tree = new Tree(new GridPoint2(1, 0));
        field.getModel().addObject(tree);
        tank(field, 0, 0, Direction.RIGHT).shoot();
        assertTrue(bullets(field).isEmpty());
        assertTrue(field.getModel().getObjects().contains(tree));
    }

    @Test
    public void validatesHealthDamageAndTimeSettings() {
        TankField field = field(25, List.of());
        Tank tank = tank(field, 0, 0, Direction.RIGHT);
        assertThrows(IllegalArgumentException.class, () -> tank.takeDamage(-1));
        assertThrows(IllegalArgumentException.class,
                () -> new TankField(tile -> true, new GameModel(), 0, new WeaponSettings(1, 1f, 1f)));
        for (float value : new float[]{0f, -1f, Float.NaN, Float.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> new WeaponSettings(1, value, 1f));
            assertThrows(IllegalArgumentException.class, () -> new WeaponSettings(1, 1f, value));
            assertThrows(IllegalArgumentException.class,
                    () -> new Bullet(new GridPoint2(), Direction.UP, tank, 1, value, field));
        }
        assertThrows(IllegalArgumentException.class, () -> new WeaponSettings(0, 1f, 1f));
        assertThrows(IllegalArgumentException.class,
                () -> new Bullet(new GridPoint2(), Direction.UP, tank, 0, 1f, field));
        tank.shoot();
        Bullet bullet = bullets(field).get(0);
        for (float value : new float[]{-1f, Float.NaN, Float.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> tank.update(value));
            assertThrows(IllegalArgumentException.class, () -> bullet.update(value));
            assertThrows(IllegalArgumentException.class, () -> field.getModel().update(value));
        }
        tank.takeDamage(0);
        assertEquals(100, tank.getHealth());
    }

    private static TankField field(int damage, List<Tree> trees) {
        return new TankField(new Level(8, 8, new GridPoint2(0, 0), trees),
                new GameModel(0.05f), 100, new WeaponSettings(damage, 0.1f, 0.5f));
    }

    private static Tank tank(TankField field, int x, int y, Direction direction) {
        return field.createTank(new GridPoint2(x, y), 0.4f, direction);
    }

    private static List<Bullet> bullets(TankField field) {
        return field.getModel().getObjects().stream().filter(Bullet.class::isInstance)
                .map(Bullet.class::cast).collect(Collectors.toList());
    }
}
