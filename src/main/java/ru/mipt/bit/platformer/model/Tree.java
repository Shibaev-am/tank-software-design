package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public class Tree extends GameObject implements ProjectileTarget {
    public Tree(GridPoint2 coordinates) {
        super(coordinates);
    }

    @Override
    public boolean occupies(GridPoint2 tile) {
        return coordinates.equals(tile);
    }

    @Override
    public void hitBy(Bullet bullet) {
        bullet.destroy();
    }
}
