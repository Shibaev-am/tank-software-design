package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class TankDamageStateTest {
    @Parameterized.Parameters(name = "{1}/{0} health, movement x{2}, shooting {3}")
    public static Collection<Object[]> cases() {
        return Arrays.asList(new Object[][]{
                {100, 100, 1, true},
                {100, 71, 1, true},
                {100, 70, 2, true},
                {100, 69, 2, true},
                {100, 16, 2, true},
                {100, 15, 2, true},
                {100, 14, 3, false},
                {100, 1, 3, false},
                {80, 80, 1, true},
                {80, 57, 1, true},
                {80, 56, 2, true},
                {80, 12, 2, true},
                {80, 11, 3, false},
                {Integer.MAX_VALUE, Integer.MAX_VALUE, 1, true},
                {Integer.MAX_VALUE, 1503238553, 1, true},
                {Integer.MAX_VALUE, 1503238552, 2, true},
                {Integer.MAX_VALUE, 322122548, 2, true},
                {Integer.MAX_VALUE, 322122547, 3, false}
        });
    }

    private final TankField field;
    private final Tank tank;
    private final int durationFactor;
    private final boolean canShoot;

    public TankDamageStateTest(int maxHealth, int health, int durationFactor, boolean canShoot) {
        field = new TankField(tile -> tile.x >= 0 && tile.y >= 0, new GameModel(), maxHealth,
                new WeaponSettings(25, 0.1f, 0.5f));
        tank = field.createTank(new GridPoint2(), 0.4f, Direction.RIGHT);
        tank.takeDamage(maxHealth - health);
        this.durationFactor = durationFactor;
        this.canShoot = canShoot;
    }

    @Test
    public void movementDurationDependsOnRemainingFractionOfInitialHealth() {
        tank.move(Direction.RIGHT);
        tank.update(0.2f * durationFactor);
        assertEquals(0.5f, tank.getMovementProgress(), 0.0001f);
        assertEquals(new GridPoint2(), tank.getCoordinates());
        assertTrue(tank.occupies(new GridPoint2()));
        assertTrue(tank.occupies(new GridPoint2(1, 0)));
        tank.update(0.2f * durationFactor);
        assertFalse(tank.isMoving());
        assertEquals(new GridPoint2(1, 0), tank.getCoordinates());
        assertFalse(tank.occupies(new GridPoint2()));
    }

    @Test
    public void shootingDependsOnDamageStateEvenWhileMoving() {
        tank.move(Direction.RIGHT);
        tank.shoot();
        assertEquals(canShoot ? 1L : 0L,
                field.getModel().getObjects().stream().filter(Bullet.class::isInstance).count());
    }

    @Test
    public void damageStateDoesNotBypassFieldBoundaries() {
        tank.move(Direction.LEFT);
        tank.update(10f);
        assertFalse(tank.isMoving());
        assertEquals(Direction.LEFT, tank.getDirection());
        assertEquals(new GridPoint2(), tank.getCoordinates());
    }
}
