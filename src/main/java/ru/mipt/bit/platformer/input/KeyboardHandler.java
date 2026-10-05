package ru.mipt.bit.platformer.input;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.IntPredicate;

public class KeyboardHandler implements InputHandler, KeyBindings {
    private final IntPredicate isKeyPressed;
    private final List<KeyBinding> bindings = new ArrayList<>();

    public KeyboardHandler(IntPredicate isKeyPressed) {
        this.isKeyPressed = Objects.requireNonNull(isKeyPressed);
    }

    @Override
    public void bindHeld(Runnable action, int... keys) {
        bindings.add(new KeyBinding(action, true, keys));
    }

    @Override
    public void bindPressed(Runnable action, int... keys) {
        bindings.add(new KeyBinding(action, false, keys));
    }

    @Override
    public void update() {
        for (KeyBinding binding : bindings) {
            binding.update(isKeyPressed);
        }
    }

    private static class KeyBinding {
        private final Runnable action;
        private final boolean repeat;
        private final int[] keys;
        private boolean wasPressed;

        private KeyBinding(Runnable action, boolean repeat, int[] keys) {
            this.action = Objects.requireNonNull(action);
            if (keys.length == 0) {
                throw new IllegalArgumentException("At least one key is required");
            }
            this.repeat = repeat;
            this.keys = keys.clone();
        }

        private void update(IntPredicate isKeyPressed) {
            boolean pressed = false;
            for (int key : keys) {
                if (isKeyPressed.test(key)) {
                    pressed = true;
                    break;
                }
            }
            if (pressed && (repeat || !wasPressed)) {
                action.run();
            }
            wasPressed = pressed;
        }
    }
}
