package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

import static ru.mipt.bit.platformer.graphics.RenderUtils.*;

public abstract class SpriteView implements Disposable {
    private final Texture texture;
    private final TextureRegion graphics;
    private final Rectangle rectangle;
    private final TileMovement tileMovement;

    protected SpriteView(String texturePath, TiledMapTileLayer groundLayer) {
        texture = new Texture(texturePath);
        graphics = new TextureRegion(texture);
        rectangle = createBoundingRectangle(graphics);
        tileMovement = new TileMovement(groundLayer, Interpolation.smooth);
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
