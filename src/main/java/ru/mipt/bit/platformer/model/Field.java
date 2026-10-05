package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.util.TileMovement;

import java.util.ArrayList;
import java.util.List;

import static ru.mipt.bit.platformer.util.GdxGameUtils.moveRectangleAtTileCenter;

public class Field {
    private final TiledMapTileLayer groundLayer;
    private final TileMovement tileMovement;
    private final List<Tree> trees = new ArrayList<>();

    public Field(TiledMapTileLayer groundLayer) {
        this.groundLayer = groundLayer;
        this.tileMovement = new TileMovement(groundLayer, Interpolation.smooth);
    }

    public void addTree(Tree tree) {
        tree.placeOn(this);
        trees.add(tree);
    }

    public boolean isPassable(GridPoint2 tile) {
        for (Tree tree : trees) {
            if (tree.occupies(tile)) {
                return false;
            }
        }
        return true;
    }

    public void placeAtTileCenter(Rectangle rectangle, GridPoint2 tile) {
        moveRectangleAtTileCenter(groundLayer, rectangle, tile);
    }

    public void moveBetweenTiles(Rectangle rectangle, GridPoint2 from, GridPoint2 to, float progress) {
        tileMovement.moveRectangleBetweenTileCenters(rectangle, from, to, progress);
    }

    public void render(Batch batch) {
        for (Tree tree : trees) {
            tree.render(batch);
        }
    }
}
