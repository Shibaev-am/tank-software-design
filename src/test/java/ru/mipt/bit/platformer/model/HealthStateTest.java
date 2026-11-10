package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import static org.junit.Assert.*;

public class HealthStateTest {
    @Test
    public void tankStartsWithFullHealth() {
        Tank tank = new TankField(tile -> true).createTank(new GridPoint2(), 0.4f, Direction.RIGHT);
        assertEquals(100, tank.getHealth());
        assertEquals(100, tank.getMaxHealth());
        assertEquals(1f, tank.getHealthFraction(), 0f);
    }

    @Test
    public void damageChangesTheFractionButNotTheInitialMaximum() {
        TankField field = new TankField(tile -> true, new GameModel(), 80, new WeaponSettings(25, 0.1f, 0.5f));
        Tank tank = field.createTank(new GridPoint2(), 0.4f, Direction.RIGHT);
        tank.takeDamage(20);
        assertEquals(80, tank.getMaxHealth());
        assertEquals(60, tank.getHealth());
        assertEquals(0.75f, tank.getHealthFraction(), 0f);
        tank.takeDamage(100);
        assertEquals(80, tank.getMaxHealth());
        assertEquals(0f, tank.getHealthFraction(), 0f);
    }

    @Test
    public void bulletHasOneLifeUntilCollision() {
        TankField field = new TankField(tile -> true);
        Tank shooter = field.createTank(new GridPoint2(), 0.4f, Direction.RIGHT);
        shooter.shoot();
        Bullet bullet = (Bullet) field.getModel().getObjects().get(1);
        assertEquals(1, bullet.getHealth());
        assertEquals(1, bullet.getMaxHealth());
        assertEquals(1f, bullet.getHealthFraction(), 0f);
        new Tree(bullet.getCoordinates()).hitBy(bullet);
        assertEquals(0, bullet.getHealth());
        assertEquals(1, bullet.getMaxHealth());
        assertEquals(0f, bullet.getHealthFraction(), 0f);
        assertFalse(field.getModel().getObjects().contains(bullet));
    }
}
