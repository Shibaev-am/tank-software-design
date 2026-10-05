package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public abstract class GameObject {
    protected final GridPoint2 coordinates;

    protected GameObject(GridPoint2 coordinates) {
        this.coordinates = new GridPoint2(coordinates);
    }

    public GridPoint2 getCoordinates() {
        return new GridPoint2(coordinates);
    }
}
