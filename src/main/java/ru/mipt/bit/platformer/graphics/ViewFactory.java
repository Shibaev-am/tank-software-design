package ru.mipt.bit.platformer.graphics;

import ru.mipt.bit.platformer.model.Updatable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class ViewFactory implements Function<Updatable, SpriteRenderer> {
    private final Map<Class<?>, Function<Updatable, SpriteRenderer>> factories = new HashMap<>();

    public <T extends Updatable> void register(Class<T> type, Function<T, SpriteRenderer> factory) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(factory);
        factories.put(type, object -> factory.apply(type.cast(object)));
    }

    @Override
    public SpriteRenderer apply(Updatable object) {
        Function<Updatable, SpriteRenderer> factory = factories.get(object.getClass());
        if (factory == null) {
            throw new IllegalArgumentException("No view for " + object.getClass().getSimpleName());
        }
        return factory.apply(object);
    }
}
