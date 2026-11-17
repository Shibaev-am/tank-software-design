package ru.mipt.bit.platformer.control;

/** Порт application: собрать команды для текущего кадра. */
@FunctionalInterface
public interface ControlStrategy {
    void update();
}
