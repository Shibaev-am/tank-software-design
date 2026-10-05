package ru.mipt.bit.platformer.input;

public interface KeyBindings {
    void bindHeld(Runnable action, int... keys);
    void bindPressed(Runnable action, int... keys);
}
