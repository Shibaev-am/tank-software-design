package ru.mipt.bit.platformer.command;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;

public final class CommandQueue implements Command {
    private final Queue<Command> commands = new ArrayDeque<>();

    public void submit(Command command) {
        commands.add(Objects.requireNonNull(command));
    }

    public Command defer(Command command) {
        Objects.requireNonNull(command);
        return () -> submit(command);
    }

    @Override
    public void execute() {
        int count = commands.size();
        for (int i = 0; i < count; i++) {
            commands.remove().execute();
        }
    }
}
