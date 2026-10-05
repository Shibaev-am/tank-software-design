package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Field {
    private final List<Tree> trees = new ArrayList<>();

    public void addTree(Tree tree) {
        trees.add(tree);
    }

    public List<Tree> getTrees() {
        return Collections.unmodifiableList(trees);
    }

    public boolean isPassable(GridPoint2 tile) {
        for (Tree tree : trees) {
            if (tree.occupies(tile)) {
                return false;
            }
        }
        return true;
    }
}
