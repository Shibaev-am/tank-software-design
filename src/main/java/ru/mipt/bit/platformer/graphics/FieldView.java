package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.Tree;

import static ru.mipt.bit.platformer.graphics.RenderUtils.createSingleLayerMapRenderer;
import static ru.mipt.bit.platformer.graphics.RenderUtils.getSingleLayer;

public class FieldView implements Disposable {
    private final Field field;
    private final TiledMap level;
    private final OrthogonalTiledMapRenderer levelRenderer;
    private final TiledMapTileLayer groundLayer;
    private final TreeView treeView;

    public FieldView(Field field, Batch batch) {
        this.field = field;
        level = new TmxMapLoader().load("level.tmx");
        groundLayer = getSingleLayer(level);
        levelRenderer = createSingleLayerMapRenderer(level, batch);
        treeView = new TreeView(groundLayer);
    }

    TiledMapTileLayer getGroundLayer() {
        return groundLayer;
    }

    public void renderGround() {
        levelRenderer.render();
    }

    public void renderTrees(Batch batch) {
        for (Tree tree : field.getTrees()) {
            treeView.render(batch, tree);
        }
    }

    @Override
    public void dispose() {
        treeView.dispose();
        levelRenderer.dispose();
        level.dispose();
    }
}
