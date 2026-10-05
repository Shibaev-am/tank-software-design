package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Tank;

import static com.badlogic.gdx.Input.Keys.*;

public final class TankControls {
    private TankControls() {
    }

    public static void bindMovement(KeyboardHandler keyboard, Tank tank) {
        keyboard.bindHeld(() -> tank.move(Direction.UP), UP, W);
        keyboard.bindHeld(() -> tank.move(Direction.LEFT), LEFT, A);
        keyboard.bindHeld(() -> tank.move(Direction.DOWN), DOWN, S);
        keyboard.bindHeld(() -> tank.move(Direction.RIGHT), RIGHT, D);
    }
}
