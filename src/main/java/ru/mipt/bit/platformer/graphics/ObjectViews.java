package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import ru.mipt.bit.platformer.model.GameModel;
import ru.mipt.bit.platformer.model.ModelListener;
import ru.mipt.bit.platformer.model.Updatable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class ObjectViews implements ModelListener, SpriteRenderer {
    private final GameModel model;
    private final Function<Updatable, SpriteRenderer> factory;
    private final Map<Updatable, SpriteRenderer> views = new LinkedHashMap<>();

    public ObjectViews(GameModel model, Function<Updatable, SpriteRenderer> factory) {
        this.model = Objects.requireNonNull(model);
        this.factory = Objects.requireNonNull(factory);
        model.addListener(this);
    }

    @Override
    public void objectAdded(Updatable object) {
        views.computeIfAbsent(object, key -> Objects.requireNonNull(factory.apply(key)));
    }

    @Override
    public void objectRemoved(Updatable object) {
        SpriteRenderer view = views.remove(object);
        if (view != null) {
            view.dispose();
        }
    }

    @Override
    public void render(Batch batch) {
        views.values().forEach(view -> view.render(batch));
    }

    @Override
    public void dispose() {
        model.removeListener(this);
        views.values().forEach(SpriteRenderer::dispose);
        views.clear();
    }
}
