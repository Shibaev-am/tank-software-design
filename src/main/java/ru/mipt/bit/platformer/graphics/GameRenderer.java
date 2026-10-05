package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import ru.mipt.bit.platformer.Renderer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

public class GameRenderer implements Renderer {
    private final Batch batch;
    private final Renderer background;
    private final List<SpriteRenderer> views;
    private final Color clearColor;

    public GameRenderer(Batch batch, Renderer background,
                        Collection<? extends SpriteRenderer> views, Color clearColor) {
        this.batch = batch;
        this.background = background;
        this.views = new ArrayList<>(views);
        this.clearColor = new Color(clearColor);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
        background.render();
        batch.begin();
        for (SpriteRenderer view : views) {
            view.render(batch);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        for (SpriteRenderer view : views) {
            view.dispose();
        }
        background.dispose();
        batch.dispose();
    }
}
