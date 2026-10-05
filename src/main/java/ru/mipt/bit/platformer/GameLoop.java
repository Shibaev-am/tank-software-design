package ru.mipt.bit.platformer;

import ru.mipt.bit.platformer.input.InputHandler;
import ru.mipt.bit.platformer.model.Updatable;

import java.util.Objects;

public class GameLoop {
    private final InputHandler input;
    private final Updatable model;
    private final Renderer renderer;

    public GameLoop(InputHandler input, Updatable model, Renderer renderer) {
        this.input = Objects.requireNonNull(input);
        this.model = Objects.requireNonNull(model);
        this.renderer = Objects.requireNonNull(renderer);
    }

    public void render(float deltaTime) {
        input.update();
        model.update(deltaTime);
        renderer.render();
    }

    public void dispose() {
        renderer.dispose();
    }
}
