package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Field implements Passability {
    private final List<Obstacle> obstacles = new ArrayList<>();

    public void addObstacle(Obstacle obstacle) {
        obstacles.add(Objects.requireNonNull(obstacle));
    }

    public List<Obstacle> getObstacles() {
        return Collections.unmodifiableList(obstacles);
    }

    @Override
    public boolean isPassable(GridPoint2 tile) {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.occupies(tile)) {
                return false;
            }
        }
        return true;
    }
}
