package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

/** Контракт чтения движения domain для отображения. */
public interface MovementState extends Positioned {
    GridPoint2 getDestinationCoordinates();
    float getMovementProgress();
    Direction getDirection();
}
