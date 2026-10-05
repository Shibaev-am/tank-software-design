package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import ru.mipt.bit.platformer.model.Tank;

public class TankView extends SpriteView {
    private final Tank tank;

    public TankView(Tank tank, TiledMapTileLayer groundLayer) {
        super("images/tank_blue.png", groundLayer);
        this.tank = tank;
    }

    public void render(Batch batch) {
        draw(batch, tank.getCoordinates(), tank.getDestinationCoordinates(),
                tank.getMovementProgress(), tank.getDirection().getRotation());
    }
}
