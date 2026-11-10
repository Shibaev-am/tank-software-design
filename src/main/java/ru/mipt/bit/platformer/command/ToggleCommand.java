package ru.mipt.bit.platformer.command;

import java.util.Objects;

public final class ToggleCommand implements Command {
    private final Runnable toggle;

    public ToggleCommand(Runnable toggle) {
        this.toggle = Objects.requireNonNull(toggle);
    }

    @Override
    public void execute() {
        toggle.run();
    }
}
