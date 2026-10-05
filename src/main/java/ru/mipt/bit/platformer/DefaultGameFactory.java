package ru.mipt.bit.platformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import ru.mipt.bit.platformer.graphics.*;
import ru.mipt.bit.platformer.input.KeyboardHandler;
import ru.mipt.bit.platformer.input.TankControls;
import ru.mipt.bit.platformer.model.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.badlogic.gdx.Input.Keys.*;
import static ru.mipt.bit.platformer.graphics.RenderUtils.getSingleLayer;

public class DefaultGameFactory implements Supplier<GameLoop> {
    @Override
    public GameLoop get() {
        Field field = new Field();
        List<Tree> trees = Collections.singletonList(new Tree(new GridPoint2(1, 3)));
        trees.forEach(field::addObstacle);
        Tank tank = new Tank(new GridPoint2(1, 1), field, 0.4f, Direction.RIGHT);
        GameModel model = new GameModel(Collections.singletonList(tank));

        KeyboardHandler keyboard = new KeyboardHandler(key -> Gdx.input.isKeyPressed(key));
        Map<Direction, int[]> keys = new EnumMap<>(Direction.class);
        keys.put(Direction.UP, new int[]{UP, W});
        keys.put(Direction.LEFT, new int[]{LEFT, A});
        keys.put(Direction.DOWN, new int[]{DOWN, S});
        keys.put(Direction.RIGHT, new int[]{RIGHT, D});
        TankControls.bindMovement(keyboard, tank::move, keys);

        Batch batch = new SpriteBatch();
        TiledMap level = new TmxMapLoader().load("level.tmx");
        TileMovement movement = new TileMovement(getSingleLayer(level), Interpolation.smooth);
        FieldView background = new FieldView(level, batch);
        List<SpriteRenderer> views = Arrays.asList(
                new MovingObjectView(tank, "images/tank_blue.png", movement),
                new StaticObjectView(trees, "images/greenTree.png", movement, 0f));
        GameRenderer renderer = new GameRenderer(batch, background, views, new Color(0f, 0f, 0.2f, 1f));
        return new GameLoop(keyboard, model, renderer);
    }
}
