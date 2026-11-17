package ru.mipt.bit.platformer.model.state;

import ru.mipt.bit.platformer.model.HealthState;

public abstract class HealthBasedTankState implements TankState {
    private static final TankState LIGHT = new LightTankState();
    private static final TankState MEDIUM = new MediumTankState();
    private static final TankState HEAVY = new HeavyTankState();

    public static TankState forHealth(HealthState health) {
        long percent = 100L * health.getHealth();
        if (percent > 70L * health.getMaxHealth()) {
            return LIGHT;
        }
        if (percent >= 15L * health.getMaxHealth()) {
            return MEDIUM;
        }
        return HEAVY;
    }

    @Override
    public final TankState onHealthChanged(HealthState health) {
        return forHealth(health);
    }

    @Override
    public float reloadDelta(float deltaTime) {
        return deltaTime;
    }

    @Override
    public boolean canShoot() {
        return true;
    }
}
