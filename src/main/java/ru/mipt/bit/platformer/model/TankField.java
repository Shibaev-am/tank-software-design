package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class TankField implements Passability {
    private final Passability terrain;
    private final Field occupied = new Field();
    private final List<Tank> tanks = new ArrayList<>();

    public TankField(Passability terrain) {
        this.terrain = Objects.requireNonNull(terrain);
    }

    public Tank createTank(GridPoint2 coordinates, float movementDuration, Direction direction) {
        if (!isPassable(Objects.requireNonNull(coordinates))) {
            throw new IllegalArgumentException("Tank must start on a free tile");
        }
        Tank tank = new Tank(coordinates, this, movementDuration, direction);
        tanks.add(tank);
        occupied.addObstacle(tank);
        return tank;
    }

    public List<Tank> getTanks() {
        return Collections.unmodifiableList(tanks);
    }

    @Override
    public boolean isPassable(GridPoint2 tile) {
        return terrain.isPassable(tile) && occupied.isPassable(tile);
    }
}
