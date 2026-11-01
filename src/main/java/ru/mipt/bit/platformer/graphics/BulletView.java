package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import ru.mipt.bit.platformer.model.MovementState;

public final class BulletView extends SpriteView {
    private final MovementState bullet;

    public BulletView(MovementState bullet, TileMovement movement) {
        super(createTexture(), movement);
        this.bullet = bullet;
    }

    @Override
    public void render(Batch batch) {
        draw(batch, bullet.getCoordinates(), bullet.getDestinationCoordinates(),
                bullet.getMovementProgress(), bullet.getDirection().getRotation());
    }

    private static Texture createTexture() {
        Pixmap pixels = new Pixmap(12, 6, Pixmap.Format.RGBA8888);
        try {
            pixels.setColor(1f, 0.8f, 0.15f, 1f);
            pixels.fill();
            return new Texture(pixels);
        } finally {
            pixels.dispose();
        }
    }
}
