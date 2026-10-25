package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.Objects;

public class Tank extends GameObject implements MovementState, Updatable, Obstacle {
    private final Passability passability;
    private final float movementDuration;
    private final GridPoint2 destination;
    private Direction direction;
    private float movementProgress = 1f;

    public Tank(GridPoint2 coordinates, Passability passability, float movementDuration, Direction direction) {
        super(coordinates);
        if (!Float.isFinite(movementDuration) || movementDuration <= 0f) {
            throw new IllegalArgumentException("Movement duration must be positive and finite");
        }
        this.passability = Objects.requireNonNull(passability);
        this.movementDuration = movementDuration;
        this.direction = Objects.requireNonNull(direction);
        this.destination = new GridPoint2(coordinates);
    }

    public void move(Direction direction) {
        if (isMoving()) {
            return;
        }

        this.direction = Objects.requireNonNull(direction);
        GridPoint2 nextTile = direction.nextTile(coordinates);
        if (passability.isPassable(nextTile)) {
            destination.set(nextTile);
            movementProgress = 0f;
        }
    }

    @Override
    public void update(float deltaTime) {
        movementProgress = Math.min(1f, Math.max(0f, movementProgress + deltaTime / movementDuration));
        if (!isMoving()) {
            coordinates.set(destination);
        }
    }

    public boolean isMoving() {
        return movementProgress < 1f;
    }

    @Override
    public boolean occupies(GridPoint2 tile) {
        return coordinates.equals(tile) || destination.equals(tile);
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public GridPoint2 getDestinationCoordinates() {
        return new GridPoint2(destination);
    }

    @Override
    public float getMovementProgress() {
        return movementProgress;
    }
}
