package ru.mipt.bit.platformer.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GameModel implements Updatable {
    private final List<Updatable> objects = new ArrayList<>();
    private final List<ModelListener> listeners = new ArrayList<>();
    private final float maxUpdateStep;

    public GameModel() {
        this(Float.MAX_VALUE);
    }

    public GameModel(float maxUpdateStep) {
        if (!Float.isFinite(maxUpdateStep) || maxUpdateStep <= 0f) {
            throw new IllegalArgumentException("Update step must be positive and finite");
        }
        this.maxUpdateStep = maxUpdateStep;
    }

    public GameModel(Collection<? extends Updatable> objects) {
        this();
        objects.forEach(this::addObject);
    }

    public void addObject(Updatable object) {
        Objects.requireNonNull(object);
        if (!objects.contains(object)) {
            objects.add(object);
            for (ModelListener listener : List.copyOf(listeners)) {
                listener.objectAdded(object);
            }
        }
    }

    public void removeObject(Updatable object) {
        if (objects.remove(object)) {
            for (ModelListener listener : List.copyOf(listeners)) {
                listener.objectRemoved(object);
            }
        }
    }

    public List<Updatable> getObjects() {
        return Collections.unmodifiableList(objects);
    }

    public void addListener(ModelListener listener) {
        Objects.requireNonNull(listener);
        if (!listeners.contains(listener)) {
            listeners.add(listener);
            for (Updatable object : List.copyOf(objects)) {
                listener.objectAdded(object);
            }
        }
    }

    public void removeListener(ModelListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void update(float deltaTime) {
        if (!Float.isFinite(deltaTime) || deltaTime < 0f) {
            throw new IllegalArgumentException("Delta time must be non-negative and finite");
        }
        List<Updatable> frameObjects = List.copyOf(objects);
        float remaining = deltaTime;
        do {
            float step = Math.min(remaining, maxUpdateStep);
            for (Updatable object : frameObjects) {
                if (objects.contains(object)) {
                    object.update(step);
                }
            }
            remaining -= step;
        } while (remaining > 0f);
    }
}
