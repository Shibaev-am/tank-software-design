package ru.mipt.bit.platformer.model;

/** Выходной порт domain: события добавления и удаления объектов. */
public interface ModelListener {
    void objectAdded(Updatable object);
    void objectRemoved(Updatable object);
}
