package ru.mipt.bit.platformer.model.state;

public final class HeavyTankState extends HealthBasedTankState {
    @Override
    public float movementDelta(float deltaTime, float movementDuration) {
        return deltaTime / movementDuration / 3f;
    }

    @Override
    public boolean canShoot() {
        return false;
    }
}
