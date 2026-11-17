package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

/** Внутренний контракт domain: проходимость клетки. */
@FunctionalInterface
public interface Passability {
    boolean isPassable(GridPoint2 tile);
}
