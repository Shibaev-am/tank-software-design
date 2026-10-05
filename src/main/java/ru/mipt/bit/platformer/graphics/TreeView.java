package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.Tree;

public class TreeView extends SpriteView {
    public TreeView(TiledMapTileLayer groundLayer) {
        super("images/greenTree.png", groundLayer);
    }

    public void render(Batch batch, Tree tree) {
        GridPoint2 tile = tree.getCoordinates();
        draw(batch, tile, tile, 1f, 0f);
    }
}
