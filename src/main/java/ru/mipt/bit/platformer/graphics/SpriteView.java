package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;

import static ru.mipt.bit.platformer.graphics.RenderUtils.*;

public abstract class SpriteView implements SpriteRenderer {
    private final Texture texture;
    private final TextureRegion graphics;
    private final Rectangle rectangle;
    private final TileMovement tileMovement;

    protected SpriteView(String texturePath, TileMovement tileMovement) {
        this(new Texture(texturePath), tileMovement);
    }

    protected SpriteView(Texture texture, TileMovement tileMovement) {
        this.texture = texture;
        graphics = new TextureRegion(texture);
        rectangle = createBoundingRectangle(graphics);
        this.tileMovement = tileMovement;
    }

    protected void draw(Batch batch, GridPoint2 from, GridPoint2 to, float progress, float rotation) {
        tileMovement.moveRectangleBetweenTileCenters(rectangle, from, to, progress);
        drawTextureRegionUnscaled(batch, graphics, rectangle, rotation);
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
