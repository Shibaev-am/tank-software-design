package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;

import static ru.mipt.bit.platformer.util.GdxGameUtils.continueProgress;

public class Tank extends GameObject {
    private static final float MOVEMENT_DURATION = 0.4f;

    private final Field field;
    private final GridPoint2 destination;
    private Direction direction = Direction.RIGHT;
    private float movementProgress = 1f;

    public Tank(TextureRegion graphics, GridPoint2 coordinates, Field field) {
        super(graphics, coordinates);
        this.field = field;
        this.destination = new GridPoint2(coordinates);
        field.placeAtTileCenter(rectangle, coordinates);
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
        movementProgress = continueProgress(movementProgress, deltaTime, MOVEMENT_DURATION);
        field.moveBetweenTiles(rectangle, coordinates, destination, movementProgress);
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

    @Override
    protected float getRotation() {
        return direction.getRotation();
    }
}
