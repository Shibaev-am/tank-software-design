package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.Objects;

public final class Bullet extends GameObject implements MovementState, ProjectileTarget {
    private final Direction direction;
    private final Tank owner;
    private final int damage;
    private final float tileDuration;
    private final CombatWorld world;
    private float progress;
    private boolean active = true;

    public Bullet(GridPoint2 coordinates, Direction direction, Tank owner, int damage,
                  float tileDuration, CombatWorld world) {
        super(coordinates);
        if (damage <= 0 || !Float.isFinite(tileDuration) || tileDuration <= 0f) {
            throw new IllegalArgumentException("Bullet damage and duration must be positive and finite");
        }
        this.direction = Objects.requireNonNull(direction);
        this.owner = Objects.requireNonNull(owner);
        this.damage = damage;
        this.tileDuration = tileDuration;
        this.world = Objects.requireNonNull(world);
    }

    @Override
    public void update(float deltaTime) {
        if (!Float.isFinite(deltaTime) || deltaTime < 0f) {
            throw new IllegalArgumentException("Delta time must be non-negative and finite");
        }
        if (!active || world.collide(this)) {
            return;
        }
        float remaining = deltaTime;
        while (active && remaining > 0f) {
            float untilNextTile = (1f - progress) * tileDuration;
            if (remaining < untilNextTile) {
                progress += remaining / tileDuration;
                world.collide(this);
                return;
            }
            remaining -= untilNextTile;
            coordinates.set(direction.nextTile(coordinates));
            progress = 0f;
            if (world.collide(this)) {
                return;
            }
        }
    }

    public void destroy() {
        if (active) {
            active = false;
            world.remove(this);
        }
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public void hitBy(Bullet bullet) {
        destroy();
        bullet.destroy();
    }

    public Tank getOwner() {
        return owner;
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public boolean occupies(GridPoint2 tile) {
        return active && (coordinates.equals(tile)
                || (progress > 0f && getDestinationCoordinates().equals(tile)));
    }

    @Override
    public GridPoint2 getDestinationCoordinates() {
        return direction.nextTile(coordinates);
    }

    @Override
    public float getMovementProgress() {
        return progress;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }
}
