package ru.mipt.bit.platformer.model;

@FunctionalInterface
public interface Updatable {
    void update(float deltaTime);
}
