package ru.mipt.bit.platformer.command;

/** Входной порт application: выполнить игровое действие. */
@FunctionalInterface
public interface Command {
    void execute();
}
