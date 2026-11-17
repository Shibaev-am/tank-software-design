package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.state.HealthBasedTankState;
import ru.mipt.bit.platformer.model.state.TankState;

import java.util.Objects;

public class Tank extends GameObject implements MovementState, ProjectileTarget, HealthState {
    private final Passability passability;
    private final float movementDuration;
    private final GridPoint2 destination;
    private Direction direction;
    private float movementProgress = 1f;
    private final CombatWorld world;
    private final WeaponSettings weapon;
    private int health;
    private final int maxHealth;
    private float reloadRemaining;
    private TankState state;

    public Tank(GridPoint2 coordinates, Passability passability, float movementDuration, Direction direction,
                int health, WeaponSettings weapon, CombatWorld world) {
        super(coordinates);
        if (!Float.isFinite(movementDuration) || movementDuration <= 0f) {
            throw new IllegalArgumentException("Movement duration must be positive and finite");
        }
        this.passability = Objects.requireNonNull(passability);
        this.movementDuration = movementDuration;
        this.direction = Objects.requireNonNull(direction);
        this.destination = new GridPoint2(coordinates);
        if (health <= 0) {
            throw new IllegalArgumentException("Health must be positive");
        }
        this.health = health;
        this.maxHealth = health;
        this.weapon = Objects.requireNonNull(weapon);
        this.world = Objects.requireNonNull(world);
        this.state = HealthBasedTankState.forHealth(this);
    }

    public void move(Direction direction) {
        if (!isAlive() || isMoving()) {
            return;
        }

        this.direction = Objects.requireNonNull(direction);
        GridPoint2 nextTile = direction.nextTile(coordinates);
        if (passability.isPassable(nextTile)) {
            destination.set(nextTile);
            movementProgress = 0f;
        }
    }

    @Override
    public void update(float deltaTime) {
        if (!Float.isFinite(deltaTime) || deltaTime < 0f) {
            throw new IllegalArgumentException("Delta time must be non-negative and finite");
        }
        if (!isAlive()) {
            return;
        }
        world.collide(this);
        if (!isAlive()) {
            return;
        }
        reloadRemaining = Math.max(0f, reloadRemaining - state.reloadDelta(deltaTime));
        movementProgress = Math.min(1f, Math.max(0f,
                movementProgress + state.movementDelta(deltaTime, movementDuration)));
        if (!isMoving()) {
            coordinates.set(destination);
        }
    }

    public void shoot() {
        if (isAlive() && state.canShoot() && reloadRemaining <= 0f) {
            reloadRemaining = weapon.getReloadDuration();
            world.addBullet(new Bullet(direction.nextTile(coordinates), direction, this,
                    weapon.getDamage(), weapon.getBulletTileDuration(), world));
        }
    }

    public void takeDamage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("Damage cannot be negative");
        }
        if (isAlive() && damage > 0) {
            health = Math.max(0, health - damage);
            if (!isAlive()) {
                world.remove(this);
            } else {
                changeState(state.onHealthChanged(this));
            }
        }
    }

    public void changeState(TankState state) {
        this.state = Objects.requireNonNull(state);
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public int getMaxHealth() {
        return maxHealth;
    }

    @Override
    public void hitBy(Bullet bullet) {
        takeDamage(bullet.getDamage());
        bullet.destroy();
    }

    public boolean isAlive() {
        return health > 0;
    }

    public boolean isMoving() {
        return movementProgress < 1f;
    }

    @Override
    public boolean occupies(GridPoint2 tile) {
        return isAlive() && (coordinates.equals(tile) || destination.equals(tile));
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public GridPoint2 getDestinationCoordinates() {
        return new GridPoint2(destination);
    }

    @Override
    public float getMovementProgress() {
        return movementProgress;
    }
}
