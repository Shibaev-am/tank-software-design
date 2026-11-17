package ru.mipt.bit.platformer.model.state;

public final class LightTankState extends HealthBasedTankState {
    @Override
    public float movementDelta(float deltaTime, float movementDuration) {
        return deltaTime / movementDuration;
    }
}
