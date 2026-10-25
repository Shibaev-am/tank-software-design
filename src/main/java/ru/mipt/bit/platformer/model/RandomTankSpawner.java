package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class RandomTankSpawner {
    private final int width;
    private final int height;
    private final int count;
    private final float movementDuration;
    private final Random random;

    public RandomTankSpawner(int width, int height, int count, float movementDuration, Random random) {
        if (width <= 0 || height <= 0 || (long) width * height > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Invalid field dimensions");
        }
        if (count < 0) {
            throw new IllegalArgumentException("Tank count cannot be negative");
        }
        if (!Float.isFinite(movementDuration) || movementDuration <= 0f) {
            throw new IllegalArgumentException("Movement duration must be positive and finite");
        }
        this.width = width;
        this.height = height;
        this.count = count;
        this.movementDuration = movementDuration;
        this.random = Objects.requireNonNull(random);
    }

    public List<Tank> spawn(TankField field) {
        Objects.requireNonNull(field);
        List<GridPoint2> freeTiles = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                GridPoint2 tile = new GridPoint2(x, y);
                if (field.isPassable(tile)) {
                    freeTiles.add(tile);
                }
            }
        }
        Collections.shuffle(freeTiles, random);
        List<Tank> spawned = new ArrayList<>();
        Direction[] directions = Direction.values();
        for (int i = 0; i < Math.min(count, freeTiles.size()); i++) {
            spawned.add(field.createTank(freeTiles.get(i), movementDuration,
                    directions[random.nextInt(directions.length)]));
        }
        return Collections.unmodifiableList(spawned);
    }
}
