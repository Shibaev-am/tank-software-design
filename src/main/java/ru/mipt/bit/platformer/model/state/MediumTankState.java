package ru.mipt.bit.platformer.model.state;

public final class MediumTankState extends HealthBasedTankState {
    @Override
    public float movementDelta(float deltaTime, float movementDuration) {
        return deltaTime / movementDuration / 2f;
    }
}
