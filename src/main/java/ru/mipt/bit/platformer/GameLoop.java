package ru.mipt.bit.platformer;

import ru.mipt.bit.platformer.control.ControlStrategy;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.model.Updatable;

import java.util.Objects;

public class GameLoop {
    private final ControlStrategy control;
    private final Command commands;
    private final Updatable model;
    private final Renderer renderer;

    public GameLoop(ControlStrategy control, Command commands, Updatable model, Renderer renderer) {
        this.control = Objects.requireNonNull(control);
        this.commands = Objects.requireNonNull(commands);
        this.model = Objects.requireNonNull(model);
        this.renderer = Objects.requireNonNull(renderer);
    }

    public void render(float deltaTime) {
        control.update();
        commands.execute();
        model.update(deltaTime);
        renderer.render();
    }

    public void dispose() {
        renderer.dispose();
    }
}
