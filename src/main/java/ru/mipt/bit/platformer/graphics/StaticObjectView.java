package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.Positioned;

public class StaticObjectView extends SpriteView {
    private final Iterable<? extends Positioned> objects;
    private final float rotation;

    public StaticObjectView(Iterable<? extends Positioned> objects, String texturePath,
                            TileMovement tileMovement, float rotation) {
        super(texturePath, tileMovement);
        this.objects = objects;
        this.rotation = rotation;
    }

    @Override
    public void render(Batch batch) {
        for (Positioned object : objects) {
            GridPoint2 tile = object.getCoordinates();
            draw(batch, tile, tile, 1f, rotation);
        }
    }
}
