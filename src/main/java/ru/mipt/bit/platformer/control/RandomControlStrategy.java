package ru.mipt.bit.platformer.control;

import ru.mipt.bit.platformer.command.Command;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.BooleanSupplier;

public final class RandomControlStrategy implements ControlStrategy {
    private final BooleanSupplier isBusy;
    private final List<Command> commands;
    private final Random random;

    public RandomControlStrategy(BooleanSupplier isBusy, Collection<? extends Command> commands, Random random) {
        this.isBusy = Objects.requireNonNull(isBusy);
        this.commands = List.copyOf(commands);
        if (this.commands.isEmpty()) {
            throw new IllegalArgumentException("At least one command is required");
        }
        this.random = Objects.requireNonNull(random);
    }

    @Override
    public void update() {
        if (!isBusy.getAsBoolean()) {
            commands.get(random.nextInt(commands.size())).execute();
        }
    }
}
