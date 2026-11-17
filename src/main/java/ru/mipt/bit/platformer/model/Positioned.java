package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

/** Контракт чтения координат domain для отображения. */
@FunctionalInterface
public interface Positioned {
    GridPoint2 getCoordinates();
}
