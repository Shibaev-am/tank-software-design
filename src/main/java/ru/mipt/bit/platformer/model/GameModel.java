package ru.mipt.bit.platformer.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class GameModel implements Updatable {
    private final List<Updatable> objects = new ArrayList<>();

    public GameModel(Collection<? extends Updatable> objects) {
        objects.forEach(this::addObject);
    }

    public void addObject(Updatable object) {
        objects.add(Objects.requireNonNull(object));
    }

    @Override
    public void update(float deltaTime) {
        for (Updatable object : objects) {
            object.update(deltaTime);
        }
    }
}
