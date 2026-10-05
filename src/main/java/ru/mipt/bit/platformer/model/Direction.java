package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public enum Direction {
    UP(0, 1, 90f),
    LEFT(-1, 0, -180f),
    DOWN(0, -1, -90f),
    RIGHT(1, 0, 0f);

    private final GridPoint2 vector;
    private final float rotation;

    Direction(int dx, int dy, float rotation) {
        this.vector = new GridPoint2(dx, dy);
        this.rotation = rotation;
    }

    public GridPoint2 nextTile(GridPoint2 coordinates) {
        return new GridPoint2(coordinates).add(vector);
    }

    public GridPoint2 getVector() {
        return new GridPoint2(vector);
    }

    public float getRotation() {
        return rotation;
    }
}
