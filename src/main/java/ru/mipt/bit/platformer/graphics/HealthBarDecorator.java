package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.Objects;
import java.util.function.BooleanSupplier;

public final class HealthBarDecorator implements SpriteRenderer {
    private final SpriteRenderer object;
    private final SpriteRenderer healthBar;
    private final BooleanSupplier visible;

    public HealthBarDecorator(SpriteRenderer object, SpriteRenderer healthBar, BooleanSupplier visible) {
        this.object = Objects.requireNonNull(object);
        this.healthBar = Objects.requireNonNull(healthBar);
        this.visible = Objects.requireNonNull(visible);
    }

    @Override
    public void render(Batch batch) {
        object.render(batch);
        if (visible.getAsBoolean()) {
            healthBar.render(batch);
        }
    }

    @Override
    public void dispose() {
        try {
            object.dispose();
        } finally {
            healthBar.dispose();
        }
    }
}
