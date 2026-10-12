package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.Tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class RandomLevelLoader implements LevelLoader {
    private final int width;
    private final int height;
    private final int treeCount;
    private final Random random;

    public RandomLevelLoader(int width, int height, int treeCount, Random random) {
        long cells = (long) width * height;
        if (width <= 0 || height <= 0 || cells > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Level dimensions must be positive and fit in an integer");
        }
        if (treeCount < 0 || treeCount >= cells) {
            throw new IllegalArgumentException("Tree count must leave at least one free tile for the player");
        }
        this.width = width;
        this.height = height;
        this.treeCount = treeCount;
        this.random = Objects.requireNonNull(random);
    }

    @Override
    public Level load() {
        List<GridPoint2> tiles = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tiles.add(new GridPoint2(x, y));
            }
        }
        Collections.shuffle(tiles, random);
        List<Tree> trees = new ArrayList<>();
        for (int i = 1; i <= treeCount; i++) {
            trees.add(new Tree(tiles.get(i)));
        }
        return new Level(width, height, tiles.get(0), trees);
    }
}
