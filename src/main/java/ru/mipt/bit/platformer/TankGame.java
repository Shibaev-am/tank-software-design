package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import java.util.Objects;
import java.util.function.Supplier;

public class TankGame extends ApplicationAdapter {
    private final Supplier<GameLoop> createLoop;
    private GameLoop loop;

    public TankGame(Supplier<GameLoop> createLoop) {
        this.createLoop = Objects.requireNonNull(createLoop);
    }

    @Override
    public void create() {
        loop = createLoop.get();
    }

    @Override
    public void render() {
        loop.render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void dispose() {
        loop.dispose();
    }
}
