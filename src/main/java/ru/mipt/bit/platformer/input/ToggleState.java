package ru.mipt.bit.platformer.input;

import java.util.function.BooleanSupplier;

public final class ToggleState implements BooleanSupplier {
    private boolean enabled;

    public ToggleState(boolean enabled) {
        this.enabled = enabled;
    }

    public void toggle() {
        enabled = !enabled;
    }

    @Override
    public boolean getAsBoolean() {
        return enabled;
    }
}
