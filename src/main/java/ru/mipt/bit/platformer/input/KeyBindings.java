package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.command.Command;

public interface KeyBindings {
    void bindHeld(Command action, int... keys);
    void bindPressed(Command action, int... keys);
}
