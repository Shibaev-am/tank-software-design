package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import ru.mipt.bit.platformer.Renderer;

import static ru.mipt.bit.platformer.graphics.RenderUtils.createSingleLayerMapRenderer;

public class FieldView implements Renderer {
    private final TiledMap level;
    private final OrthogonalTiledMapRenderer levelRenderer;

    public FieldView(TiledMap level, Batch batch) {
        this.level = level;
        levelRenderer = createSingleLayerMapRenderer(level, batch);
    }

    @Override
    public void render() {
        levelRenderer.render();
    }

    @Override
    public void dispose() {
        levelRenderer.dispose();
        level.dispose();
    }
}
