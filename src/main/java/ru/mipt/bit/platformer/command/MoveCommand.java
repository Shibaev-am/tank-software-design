package ru.mipt.bit.platformer.command;

import ru.mipt.bit.platformer.model.Direction;

import java.util.Objects;
import java.util.function.Consumer;

public final class MoveCommand implements Command {
    private final Consumer<Direction> move;
    private final Direction direction;

    public MoveCommand(Consumer<Direction> move, Direction direction) {
        this.move = Objects.requireNonNull(move);
        this.direction = Objects.requireNonNull(direction);
    }

    @Override
    public void execute() {
        move.accept(direction);
    }
}
