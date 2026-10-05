package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;

import static ru.mipt.bit.platformer.util.GdxGameUtils.createBoundingRectangle;
import static ru.mipt.bit.platformer.util.GdxGameUtils.drawTextureRegionUnscaled;

public abstract class GameObject {
    private final TextureRegion graphics;
    protected final GridPoint2 coordinates;
    protected final Rectangle rectangle;

    protected GameObject(TextureRegion graphics, GridPoint2 coordinates) {
        this.graphics = graphics;
        this.coordinates = new GridPoint2(coordinates);
        this.rectangle = createBoundingRectangle(graphics);
    }

    public GridPoint2 getCoordinates() {
        return new GridPoint2(coordinates);
    }

    public void render(Batch batch) {
        drawTextureRegionUnscaled(batch, graphics, rectangle, getRotation());
    }

    protected float getRotation() {
        return 0f;
    }
}
