package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.HealthState;

import java.util.Objects;

public final class HealthBarView implements SpriteRenderer {
    private final HealthState health;
    private final HealthBarLayout layout;
    private final Texture pixel;

    public HealthBarView(HealthState health, HealthBarLayout layout) {
        this.health = Objects.requireNonNull(health);
        this.layout = Objects.requireNonNull(layout);
        Pixmap pixels = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        try {
            pixels.setColor(1f, 1f, 1f, 1f);
            pixels.fill();
            pixel = new Texture(pixels);
        } finally {
            pixels.dispose();
        }
    }

    @Override
    public void render(Batch batch) {
        Rectangle bounds = layout.getBounds();
        float previousColor = batch.getPackedColor();
        try {
            batch.setColor(0.05f, 0.05f, 0.05f, 1f);
            batch.draw(pixel, bounds.x - 1f, bounds.y - 1f, bounds.width + 2f, bounds.height + 2f);
            batch.setColor(0.6f, 0.12f, 0.12f, 1f);
            batch.draw(pixel, bounds.x, bounds.y, bounds.width, bounds.height);
            batch.setColor(0.15f, 0.9f, 0.3f, 1f);
            batch.draw(pixel, bounds.x, bounds.y, bounds.width * health.getHealthFraction(), bounds.height);
        } finally {
            batch.setPackedColor(previousColor);
        }
    }

    @Override
    public void dispose() {
        pixel.dispose();
    }
}
