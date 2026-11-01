package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class TankField implements Passability, CombatWorld {
    private final Passability terrain;
    private final GameModel model;
    private final int tankHealth;
    private final WeaponSettings weapon;

    public TankField(Passability terrain) {
        this(terrain, new GameModel(0.05f), 100, new WeaponSettings(25, 0.1f, 0.5f));
    }

    public TankField(Passability terrain, GameModel model, int tankHealth, WeaponSettings weapon) {
        this.terrain = Objects.requireNonNull(terrain);
        this.model = Objects.requireNonNull(model);
        if (tankHealth <= 0) {
            throw new IllegalArgumentException("Tank health must be positive");
        }
        this.tankHealth = tankHealth;
        this.weapon = Objects.requireNonNull(weapon);
    }

    public Tank createTank(GridPoint2 coordinates, float movementDuration, Direction direction) {
        if (!isPassable(Objects.requireNonNull(coordinates))) {
            throw new IllegalArgumentException("Tank must start on a free tile");
        }
        Tank tank = new Tank(coordinates, this, movementDuration, direction, tankHealth, weapon, this);
        model.addObject(tank);
        return tank;
    }

    public List<Tank> getTanks() {
        return model.getObjects().stream().filter(Tank.class::isInstance).map(Tank.class::cast)
                .collect(Collectors.toUnmodifiableList());
    }

    public GameModel getModel() {
        return model;
    }

    @Override
    public void addBullet(Bullet bullet) {
        Objects.requireNonNull(bullet);
        if (bullet.isActive()) {
            model.addObject(bullet);
            collide(bullet);
        }
    }

    @Override
    public void remove(GameObject object) {
        model.removeObject(object);
    }

    @Override
    public boolean collide(Bullet bullet) {
        if (!bullet.isActive()) {
            return true;
        }
        GridPoint2 tile = bullet.getCoordinates();
        if (!terrain.isPassable(tile)) {
            bullet.destroy();
            return true;
        }
        for (Updatable object : List.copyOf(model.getObjects())) {
            if (object instanceof ProjectileTarget && object != bullet && object != bullet.getOwner()) {
                ProjectileTarget target = (ProjectileTarget) object;
                if (target.occupies(tile)) {
                    target.hitBy(bullet);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isPassable(GridPoint2 tile) {
        return terrain.isPassable(tile) && getTanks().stream().noneMatch(tank -> tank.occupies(tile));
    }

    @Override
    public void collide(Tank tank) {
        for (Updatable object : List.copyOf(model.getObjects())) {
            if (object instanceof Bullet) {
                Bullet bullet = (Bullet) object;
                if (bullet.isActive() && tank.isAlive() && bullet.getOwner() != tank
                        && tank.occupies(bullet.getCoordinates())) {
                    tank.hitBy(bullet);
                }
            }
        }
    }
}
