package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import ru.mipt.bit.platformer.model.MovementState;

public class MovingObjectView extends SpriteView {
    private final MovementState object;

    public MovingObjectView(MovementState object, String texturePath, TileMovement tileMovement) {
        super(texturePath, tileMovement);
        this.object = object;
    }

    @Override
    public void render(Batch batch) {
        draw(batch, object.getCoordinates(), object.getDestinationCoordinates(),
                object.getMovementProgress(), object.getDirection().getRotation());
    }
}
