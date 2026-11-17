package ru.mipt.bit.platformer.model.state;

import ru.mipt.bit.platformer.model.HealthState;

/** Domain: прирост пути в клетках и перезарядки в секундах за один тик. */
public interface TankState {
    float movementDelta(float deltaTime, float movementDuration);
    float reloadDelta(float deltaTime);
    boolean canShoot();
    TankState onHealthChanged(HealthState health);
}
