package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.Passability;
import ru.mipt.bit.platformer.model.Tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Level implements Passability {
    private final int width;
    private final int height;
    private final GridPoint2 playerStart;
    private final List<Tree> trees;
    private final Field field = new Field();

    public Level(int width, int height, GridPoint2 playerStart, Collection<Tree> trees) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Level dimensions must be positive");
        }
        this.width = width;
        this.height = height;
        this.playerStart = new GridPoint2(playerStart);
        if (!contains(playerStart)) {
            throw new IllegalArgumentException("Player start is outside the level");
        }
        List<Tree> copies = new ArrayList<>();
        Set<GridPoint2> occupied = new HashSet<>();
        for (Tree tree : trees) {
            GridPoint2 tile = tree.getCoordinates();
            if (!contains(tile) || !occupied.add(tile)) {
                throw new IllegalArgumentException("Tree is outside the level or duplicates another tree");
            }
            Tree copy = new Tree(tile);
            copies.add(copy);
            field.addObstacle(copy);
        }
        this.trees = Collections.unmodifiableList(copies);
        if (!field.isPassable(playerStart)) {
            throw new IllegalArgumentException("Player start overlaps a tree");
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public GridPoint2 getPlayerStart() {
        return new GridPoint2(playerStart);
    }

    public List<Tree> getTrees() {
        return trees;
    }

    @Override
    public boolean isPassable(GridPoint2 tile) {
        return contains(tile) && field.isPassable(tile);
    }

    private boolean contains(GridPoint2 tile) {
        return tile.x >= 0 && tile.x < width && tile.y >= 0 && tile.y < height;
    }
}
