package ru.mipt.bit.platformer.model;

public interface ModelListener {
    void objectAdded(Updatable object);
    void objectRemoved(Updatable object);
}
