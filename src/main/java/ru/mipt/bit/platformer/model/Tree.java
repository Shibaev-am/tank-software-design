package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public class Tree extends GameObject {
    public Tree(GridPoint2 coordinates) {
        super(coordinates);
    }

    public boolean occupies(GridPoint2 tile) {
        return coordinates.equals(tile);
    }
}
