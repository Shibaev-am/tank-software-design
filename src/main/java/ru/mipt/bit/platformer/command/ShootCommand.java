package ru.mipt.bit.platformer.command;

import java.util.Objects;

public final class ShootCommand implements Command {
    private final Runnable shoot;

    public ShootCommand(Runnable shoot) {
        this.shoot = Objects.requireNonNull(shoot);
    }

    @Override
    public void execute() {
        shoot.run();
    }
}
