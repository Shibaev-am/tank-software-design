package ru.mipt.bit.platformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Interpolation;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.CommandQueue;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.command.ShootCommand;
import ru.mipt.bit.platformer.control.CompositeControlStrategy;
import ru.mipt.bit.platformer.control.ControlStrategy;
import ru.mipt.bit.platformer.control.RandomControlStrategy;
import ru.mipt.bit.platformer.graphics.BulletView;
import ru.mipt.bit.platformer.graphics.FieldView;
import ru.mipt.bit.platformer.graphics.GameRenderer;
import ru.mipt.bit.platformer.graphics.MovingObjectView;
import ru.mipt.bit.platformer.graphics.ObjectViews;
import ru.mipt.bit.platformer.graphics.StaticObjectView;
import ru.mipt.bit.platformer.graphics.TileMovement;
import ru.mipt.bit.platformer.graphics.ViewFactory;
import ru.mipt.bit.platformer.input.KeyboardHandler;
import ru.mipt.bit.platformer.input.TankControls;
import ru.mipt.bit.platformer.level.Level;
import ru.mipt.bit.platformer.level.LevelLoader;
import ru.mipt.bit.platformer.model.Bullet;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameModel;
import ru.mipt.bit.platformer.model.RandomTankSpawner;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.TankField;
import ru.mipt.bit.platformer.model.Tree;
import ru.mipt.bit.platformer.model.WeaponSettings;

import static com.badlogic.gdx.Input.Keys.*;
import static ru.mipt.bit.platformer.graphics.RenderUtils.resizeGroundLayer;

public class DefaultGameFactory implements Supplier<GameLoop> {
    private final LevelLoader levelLoader;

    public DefaultGameFactory(LevelLoader levelLoader) {
        this.levelLoader = Objects.requireNonNull(levelLoader);
    }

    @Override
    public GameLoop get() {
        Level layout;
        try {
            layout = levelLoader.load();
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot load level", e);
        }
        Random random = new Random();
        WeaponSettings weapon = new WeaponSettings(25, 0.1f, 0.5f);
        GameModel model = new GameModel(weapon.getBulletTileDuration() / 2f);
        TankField field = new TankField(layout, model, 100, weapon);
        CommandQueue commands = new CommandQueue();

        Batch batch = new SpriteBatch();
        TiledMap level = new TmxMapLoader().load("level.tmx");
        TiledMapTileLayer ground = resizeGroundLayer(level, layout.getWidth(), layout.getHeight());
        float mapWidth = ground.getWidth() * ground.getTileWidth();
        float mapHeight = ground.getHeight() * ground.getTileHeight();
        float aspect = (float) Gdx.graphics.getWidth() / Gdx.graphics.getHeight();
        OrthographicCamera camera = new OrthographicCamera(
                Math.max(mapWidth, mapHeight * aspect), Math.max(mapHeight, mapWidth / aspect));
        camera.position.set(mapWidth / 2f, mapHeight / 2f, 0f);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        TileMovement movement = new TileMovement(ground, Interpolation.smooth);
        FieldView background = new FieldView(level, batch, camera);
        ViewFactory viewFactory = new ViewFactory();
        viewFactory.register(Tank.class, tank -> new MovingObjectView(tank, "images/tank_blue.png", movement));
        viewFactory.register(Tree.class, tree -> new StaticObjectView(List.of(tree), "images/greenTree.png", movement, 0f));
        viewFactory.register(Bullet.class, bullet -> new BulletView(bullet, new TileMovement(ground, Interpolation.linear)));
        ObjectViews views = new ObjectViews(model, viewFactory);

        layout.getTrees().forEach(model::addObject);
        Tank player = field.createTank(layout.getPlayerStart(), 0.4f, Direction.RIGHT);
        viewFactory.register(Tank.class, tank -> new MovingObjectView(tank, "images/tank_blue.png", movement,
                new Color(1f, 0.55f, 0.55f, 1f)));
        List<Tank> bots = new RandomTankSpawner(layout.getWidth(), layout.getHeight(), 5, 0.4f, random)
                .spawn(field);
        KeyboardHandler keyboard = new KeyboardHandler(key -> Gdx.input.isKeyPressed(key));
        Map<Direction, int[]> keys = new EnumMap<>(Direction.class);
        keys.put(Direction.UP, new int[]{UP, W});
        keys.put(Direction.LEFT, new int[]{LEFT, A});
        keys.put(Direction.DOWN, new int[]{DOWN, S});
        keys.put(Direction.RIGHT, new int[]{RIGHT, D});
        TankControls.bindMovement(keyboard, direction -> commands.submit(new MoveCommand(player::move, direction)), keys);
        keyboard.bindPressed(commands.defer(new ShootCommand(player::shoot)), SPACE);
        List<ControlStrategy> controls = new ArrayList<>();
        controls.add(keyboard);
        for (Tank bot : bots) {
            List<Command> choices = new ArrayList<>();
            for (Direction direction : Direction.values()) {
                Command move = commands.defer(new MoveCommand(bot::move, direction));
                choices.add(move);
            }
            Command shoot = commands.defer(new ShootCommand(bot::shoot));
            choices.add(shoot);
            controls.add(new RandomControlStrategy(() -> !bot.isAlive() || bot.isMoving(), choices, random));
        }
        GameRenderer renderer = new GameRenderer(batch, background, List.of(views), new Color(0f, 0f, 0.2f, 1f));
        return new GameLoop(new CompositeControlStrategy(controls), commands, model, renderer);
    }
}
