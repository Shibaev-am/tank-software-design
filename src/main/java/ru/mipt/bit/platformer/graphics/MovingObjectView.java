package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import ru.mipt.bit.platformer.model.MovementState;

public class MovingObjectView extends SpriteView {
    private final MovementState object;
    private final Color tint;

    public MovingObjectView(MovementState object, String texturePath, TileMovement tileMovement) {
        this(object, texturePath, tileMovement, Color.WHITE);
    }

    public MovingObjectView(MovementState object, String texturePath, TileMovement tileMovement, Color tint) {
        super(texturePath, tileMovement);
        this.object = object;
        this.tint = new Color(tint);
    }

    @Override
    public void render(Batch batch) {
        float previousColor = batch.getPackedColor();
        batch.setColor(tint);
        try {
            draw(batch, object.getCoordinates(), object.getDestinationCoordinates(),
                    object.getMovementProgress(), object.getDirection().getRotation());
        } finally {
            batch.setPackedColor(previousColor);
        }
    }
}
