package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import ru.mipt.bit.platformer.command.CommandQueue;
import ru.mipt.bit.platformer.command.ShootCommand;
import ru.mipt.bit.platformer.model.state.HealthBasedTankState;
import ru.mipt.bit.platformer.model.state.TankState;

import static org.junit.Assert.*;

public class TankStateTransitionTest {
    @Test
    public void damageSlowsTheRemainingPartOfTheMoveWithoutResettingProgress() {
        TankField field = new TankField(tile -> true);
        Tank tank = field.createTank(new GridPoint2(), 0.4f, Direction.RIGHT);
        tank.move(Direction.RIGHT);
        tank.update(0.1f);
        tank.takeDamage(30);
        assertEquals(0.25f, tank.getMovementProgress(), 0.0001f);
        tank.update(0.2f);
        assertEquals(0.5f, tank.getMovementProgress(), 0.0001f);
        tank.takeDamage(56);
        tank.update(0.3f);
        assertEquals(0.75f, tank.getMovementProgress(), 0.0001f);
        assertFalse(field.isPassable(new GridPoint2()));
        assertFalse(field.isPassable(new GridPoint2(1, 0)));
        tank.update(0.3f);
        assertFalse(tank.isMoving());
        assertEquals(new GridPoint2(1, 0), tank.getCoordinates());
        assertTrue(field.isPassable(new GridPoint2()));
    }

    @Test
    public void collisionChangesStateBeforeMovementAdvancesInTheSameTick() {
        TankField field = new TankField(tile -> true, new GameModel(), 100,
                new WeaponSettings(86, 0.1f, 0.5f));
        Tank shooter = field.createTank(new GridPoint2(), 0.4f, Direction.RIGHT);
        Tank target = field.createTank(new GridPoint2(1, 1), 0.4f, Direction.DOWN);
        shooter.shoot();
        target.move(Direction.DOWN);
        target.update(0.3f);
        assertEquals(14, target.getHealth());
        assertEquals(0.25f, target.getMovementProgress(), 0.0001f);
        target.shoot();
        assertTrue(field.getModel().getObjects().stream().noneMatch(Bullet.class::isInstance));
    }

    @Test
    public void queuedShotChecksStateWhenExecuted() {
        RecordingWorld world = new RecordingWorld();
        Tank tank = tank(world);
        CommandQueue commands = new CommandQueue();
        commands.submit(new ShootCommand(tank::shoot));
        tank.takeDamage(86);
        commands.execute();
        tank.update(10f);
        tank.shoot();
        assertTrue(world.bullets.isEmpty());
    }

    @Test
    public void mediumStatePreservesReloadTimeAndDamageDoesNotResetIt() {
        RecordingWorld world = new RecordingWorld();
        Tank tank = tank(world);
        tank.shoot();
        tank.update(0.125f);
        tank.takeDamage(50);
        tank.update(0.25f);
        tank.shoot();
        assertEquals(1, world.bullets.size());
        tank.update(0.125f);
        tank.shoot();
        assertEquals(2, world.bullets.size());
    }

    @Test
    public void lethalDamageRemovesAHeavyTankAndPreventsFurtherActions() {
        RecordingWorld world = new RecordingWorld();
        Tank tank = tank(world);
        tank.takeDamage(86);
        tank.move(Direction.UP);
        tank.update(0.3f);
        tank.takeDamage(14);
        tank.takeDamage(1);
        tank.changeState(new BoostedState());
        tank.shoot();
        tank.move(Direction.RIGHT);
        tank.update(10f);
        assertEquals(List.of(tank), world.removed);
        assertTrue(world.bullets.isEmpty());
        assertEquals(new GridPoint2(), tank.getCoordinates());
        assertEquals(0.25f, tank.getMovementProgress(), 0.0001f);
        assertFalse(tank.occupies(new GridPoint2()));
    }

    @Test
    public void damageStatesAreIndependentForDifferentTanks() {
        RecordingWorld world = new RecordingWorld();
        Tank healthy = tank(world);
        Tank damaged = tank(world);
        damaged.takeDamage(90);
        healthy.move(Direction.RIGHT);
        damaged.move(Direction.RIGHT);
        healthy.update(0.3f);
        damaged.update(0.3f);
        assertEquals(0.75f, healthy.getMovementProgress(), 0.0001f);
        assertEquals(0.25f, damaged.getMovementProgress(), 0.0001f);
        healthy.shoot();
        damaged.shoot();
        assertEquals(1, world.bullets.size());
        assertSame(healthy, world.bullets.get(0).getOwner());
    }

    @Test
    public void anotherStateCanDoubleMovementAndFireRateWithoutChangingTank() {
        RecordingWorld world = new RecordingWorld();
        Tank tank = tank(world);
        tank.changeState(new BoostedState());
        tank.move(Direction.RIGHT);
        tank.shoot();
        tank.update(0.125f);
        assertEquals(0.625f, tank.getMovementProgress(), 0.0001f);
        tank.shoot();
        assertEquals(1, world.bullets.size());
        tank.update(0.125f);
        assertEquals(new GridPoint2(1, 0), tank.getCoordinates());
        tank.shoot();
        assertEquals(2, world.bullets.size());
    }

    @Test
    public void stateReplacementKeepsMovementAndReloadProgress() {
        RecordingWorld world = new RecordingWorld();
        Tank tank = tank(world);
        tank.move(Direction.RIGHT);
        tank.shoot();
        tank.update(0.125f);
        tank.changeState(new BoostedState());
        assertEquals(0.3125f, tank.getMovementProgress(), 0.0001f);
        tank.update(0.125f);
        assertEquals(0.9375f, tank.getMovementProgress(), 0.0001f);
        tank.shoot();
        assertEquals(1, world.bullets.size());
        tank.update(0.0625f);
        tank.shoot();
        assertEquals(2, world.bullets.size());
    }

    @Test
    public void customStateControlsItsOwnTransitionOnDamage() {
        Tank tank = tank(new RecordingWorld());
        tank.changeState(new BoostedState());
        tank.takeDamage(30);
        tank.move(Direction.RIGHT);
        tank.update(0.4f);
        assertEquals(0.5f, tank.getMovementProgress(), 0.0001f);
    }

    @Test
    public void zeroDamageDoesNotCancelACustomState() {
        Tank tank = tank(new RecordingWorld());
        tank.changeState(new BoostedState());
        tank.takeDamage(0);
        tank.move(Direction.RIGHT);
        tank.update(0.2f);
        assertFalse(tank.isMoving());
        assertEquals(new GridPoint2(1, 0), tank.getCoordinates());
    }

    @Test
    public void rejectsNullStateWithoutChangingBehavior() {
        Tank tank = tank(new RecordingWorld());
        assertThrows(NullPointerException.class, () -> tank.changeState(null));
        tank.move(Direction.RIGHT);
        tank.update(0.4f);
        assertFalse(tank.isMoving());
    }

    private static Tank tank(RecordingWorld world) {
        return new Tank(new GridPoint2(), tile -> true, 0.4f, Direction.RIGHT, 100,
                new WeaponSettings(25, 0.1f, 0.5f), world);
    }

    private static final class BoostedState implements TankState {
        @Override
        public float movementDelta(float deltaTime, float movementDuration) {
            return 2f * deltaTime / movementDuration;
        }

        @Override
        public float reloadDelta(float deltaTime) {
            return 2f * deltaTime;
        }

        @Override
        public boolean canShoot() {
            return true;
        }

        @Override
        public TankState onHealthChanged(HealthState health) {
            return HealthBasedTankState.forHealth(health);
        }
    }

    private static final class RecordingWorld implements CombatWorld {
        private final List<Bullet> bullets = new ArrayList<>();
        private final List<GameObject> removed = new ArrayList<>();

        public void addBullet(Bullet bullet) {
            bullets.add(bullet);
        }

        public void remove(GameObject object) {
            removed.add(object);
        }

        public boolean collide(Bullet bullet) {
            return false;
        }

        public void collide(Tank tank) {
        }
    }
}
