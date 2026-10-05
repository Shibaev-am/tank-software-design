package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import ru.mipt.bit.platformer.graphics.GameRenderer;
import ru.mipt.bit.platformer.input.KeyboardHandler;
import ru.mipt.bit.platformer.input.TankControls;
import ru.mipt.bit.platformer.model.GameModel;

public class TankGame extends ApplicationAdapter {
    private GameModel model;
    private KeyboardHandler keyboard;
    private GameRenderer renderer;

    @Override
    public void create() {
        model = new GameModel();
        keyboard = new KeyboardHandler(Gdx.input::isKeyPressed);
        TankControls.bindMovement(keyboard, model.getTank());
        renderer = new GameRenderer(model);
    }

    @Override
    public void render() {
        keyboard.update();
        model.update(Gdx.graphics.getDeltaTime());
        renderer.render();
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }
}
