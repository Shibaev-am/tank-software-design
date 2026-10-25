package ru.mipt.bit.platformer.control;

import java.util.Collection;
import java.util.List;

public final class CompositeControlStrategy implements ControlStrategy {
    private final List<ControlStrategy> controls;

    public CompositeControlStrategy(Collection<? extends ControlStrategy> controls) {
        this.controls = List.copyOf(controls);
    }

    @Override
    public void update() {
        controls.forEach(ControlStrategy::update);
    }
}
