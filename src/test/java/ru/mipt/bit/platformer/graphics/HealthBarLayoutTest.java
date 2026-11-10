package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import org.junit.Test;
import ru.mipt.bit.platformer.model.*;

import static org.junit.Assert.*;

public class HealthBarLayoutTest {
    private final TiledMapTileLayer ground = new TiledMapTileLayer(8, 8, 128, 128);
    private final TankField field = new TankField(tile -> true);

    @Test
    public void barFollowsTheSameSmoothMovementAsTheTank() {
        Tank tank = field.createTank(new GridPoint2(1, 1), 0.4f, Direction.RIGHT);
        HealthBarLayout bar = new HealthBarLayout(tank, new TileMovement(ground, Interpolation.smooth), 72f, 6f, 48f);
        assertBounds(bar.getBounds(), 156f, 240f, 72f, 6f);
        tank.move(Direction.RIGHT);
        tank.update(0.1f);
        assertBounds(bar.getBounds(), 176f, 240f, 72f, 6f);
        tank.update(0.1f);
        assertBounds(bar.getBounds(), 220f, 240f, 72f, 6f);
        tank.update(0.2f);
        assertBounds(bar.getBounds(), 284f, 240f, 72f, 6f);
        bar.getBounds().setPosition(999f, 999f);
        assertBounds(bar.getBounds(), 284f, 240f, 72f, 6f);
    }

    @Test
    public void barStaysHorizontalAndAboveTheTankForEveryDirection() {
        for (Direction direction : Direction.values()) {
            Tank tank = new TankField(tile -> true).createTank(new GridPoint2(2, 2), 0.4f, direction);
            HealthBarLayout bar = new HealthBarLayout(tank, new TileMovement(ground, Interpolation.smooth), 72f, 6f, 48f);
            tank.move(direction);
            tank.update(0.2f);
            GridPoint2 vector = direction.getVector();
            assertBounds(bar.getBounds(), 284f + 64f * vector.x, 368f + 64f * vector.y, 72f, 6f);
        }
    }

    @Test
    public void smallBulletBarUsesLinearMovement() {
        Tank tank = field.createTank(new GridPoint2(1, 1), 0.4f, Direction.RIGHT);
        tank.shoot();
        Bullet bullet = (Bullet) field.getModel().getObjects().get(1);
        HealthBarLayout bar = new HealthBarLayout(bullet, new TileMovement(ground, Interpolation.linear), 16f, 3f, 10f);
        bullet.update(0.025f);
        assertBounds(bar.getBounds(), 344f, 202f, 16f, 3f);
    }

    @Test
    public void rejectsInvalidDimensions() {
        Tank tank = field.createTank(new GridPoint2(), 0.4f, Direction.RIGHT);
        TileMovement movement = new TileMovement(ground, Interpolation.linear);
        for (float value : new float[]{0f, -1f, Float.NaN, Float.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> new HealthBarLayout(tank, movement, value, 6f, 48f));
            assertThrows(IllegalArgumentException.class, () -> new HealthBarLayout(tank, movement, 72f, value, 48f));
        }
        assertThrows(IllegalArgumentException.class, () -> new HealthBarLayout(tank, movement, 72f, 6f, Float.NaN));
    }

    private static void assertBounds(Rectangle actual, float x, float y, float width, float height) {
        assertEquals(x, actual.x, 0.001f);
        assertEquals(y, actual.y, 0.001f);
        assertEquals(width, actual.width, 0f);
        assertEquals(height, actual.height, 0f);
    }
}
