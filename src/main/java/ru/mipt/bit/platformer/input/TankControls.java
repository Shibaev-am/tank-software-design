package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.model.Direction;

import java.util.Map;
import java.util.function.Consumer;

public final class TankControls {
    private TankControls() {
    }

    public static void bindMovement(KeyBindings keyboard, Consumer<Direction> move,
                                    Map<Direction, int[]> keys) {
        keys.forEach((direction, aliases) -> keyboard.bindHeld(() -> move.accept(direction), aliases));
    }
}
