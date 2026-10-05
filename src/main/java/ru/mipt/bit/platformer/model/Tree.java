package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;

public class Tree extends GameObject {
    public Tree(TextureRegion graphics, GridPoint2 coordinates) {
        super(graphics, coordinates);
    }

    public boolean occupies(GridPoint2 tile) {
        return coordinates.equals(tile);
    }

    void placeOn(Field field) {
        field.placeAtTileCenter(rectangle, coordinates);
    }
}
