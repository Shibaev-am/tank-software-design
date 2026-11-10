package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.MovementState;

import java.util.Objects;

public final class HealthBarLayout {
    private final MovementState object;
    private final TileMovement movement;
    private final float width;
    private final float height;
    private final float offset;

    public HealthBarLayout(MovementState object, TileMovement movement, float width, float height, float offset) {
        this.object = Objects.requireNonNull(object);
        this.movement = Objects.requireNonNull(movement);
        if (!Float.isFinite(width) || width <= 0f || !Float.isFinite(height) || height <= 0f
                || !Float.isFinite(offset)) {
            throw new IllegalArgumentException("Invalid health bar dimensions");
        }
        this.width = width;
        this.height = height;
        this.offset = offset;
    }

    public Rectangle getBounds() {
        Rectangle bounds = new Rectangle(0f, 0f, width, height);
        movement.moveRectangleBetweenTileCenters(bounds, object.getCoordinates(),
                object.getDestinationCoordinates(), object.getMovementProgress());
        bounds.y += offset + height / 2f;
        return bounds;
    }
}
