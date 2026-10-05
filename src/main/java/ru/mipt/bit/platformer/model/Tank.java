package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public class Tank extends GameObject {
    private static final float MOVEMENT_DURATION = 0.4f;

    private final Field field;
    private final GridPoint2 destination;
    private Direction direction = Direction.RIGHT;
    private float movementProgress = 1f;

    public Tank(GridPoint2 coordinates, Field field) {
        super(coordinates);
        this.field = field;
        this.destination = new GridPoint2(coordinates);
    }

    public void move(Direction direction) {
        if (isMoving()) {
            return;
        }

        this.direction = direction;
        GridPoint2 nextTile = direction.nextTile(coordinates);
        if (field.isPassable(nextTile)) {
            destination.set(nextTile);
            movementProgress = 0f;
        }
    }

    public void update(float deltaTime) {
        movementProgress = Math.min(1f, Math.max(0f, movementProgress + deltaTime / MOVEMENT_DURATION));
        if (!isMoving()) {
            coordinates.set(destination);
        }
    }

    public boolean isMoving() {
        return movementProgress < 1f;
    }

    public Direction getDirection() {
        return direction;
    }

    public GridPoint2 getDestinationCoordinates() {
        return new GridPoint2(destination);
    }

    public float getMovementProgress() {
        return movementProgress;
    }
}
