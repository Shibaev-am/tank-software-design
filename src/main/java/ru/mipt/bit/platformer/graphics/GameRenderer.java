package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.model.GameModel;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

public class GameRenderer implements Disposable {
    private final Batch batch;
    private final FieldView fieldView;
    private final TankView tankView;

    public GameRenderer(GameModel model) {
        batch = new SpriteBatch();
        fieldView = new FieldView(model.getField(), batch);
        tankView = new TankView(model.getTank(), fieldView.getGroundLayer());
    }

    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
        fieldView.renderGround();
        batch.begin();
        tankView.render(batch);
        fieldView.renderTrees(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        tankView.dispose();
        fieldView.dispose();
        batch.dispose();
    }
}
