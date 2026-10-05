package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

@FunctionalInterface
public interface Obstacle {
    boolean occupies(GridPoint2 tile);
}
