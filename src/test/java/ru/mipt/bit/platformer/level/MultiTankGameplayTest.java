package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.GameLoop;
import ru.mipt.bit.platformer.Renderer;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.control.CompositeControlStrategy;
import ru.mipt.bit.platformer.control.ControlStrategy;
import ru.mipt.bit.platformer.control.RandomControlStrategy;
import ru.mipt.bit.platformer.input.KeyboardHandler;
import ru.mipt.bit.platformer.input.TankControls;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameModel;
import ru.mipt.bit.platformer.model.RandomTankSpawner;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.TankField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;

public class MultiTankGameplayTest {
    @Test
    public void playerWaitsForTheBotToReleaseItsSourceCell() {
        Level level = new Level(4, 1, new GridPoint2(0, 0), Collections.emptyList());
        TankField field = new TankField(level);
        Tank player = field.createTank(level.getPlayerStart(), 0.4f, Direction.RIGHT);
        Tank bot = field.createTank(new GridPoint2(1, 0), 0.4f, Direction.RIGHT);
        Set<Integer> pressed = new HashSet<>();
        KeyboardHandler keyboard = new KeyboardHandler(pressed::contains);
        TankControls.bindMovement(keyboard, player::move, Collections.singletonMap(Direction.RIGHT, new int[]{1}));
        ControlStrategy ai = new RandomControlStrategy(bot::isMoving,
                Collections.singletonList(new MoveCommand(bot::move, Direction.RIGHT)), new Random(1));
        GameLoop loop = loop(field, Arrays.asList(keyboard, ai));
        loop.render(0.1f);
        assertFalse(player.isMoving());
        assertTrue(bot.isMoving());
        pressed.add(1);
        loop.render(0.1f);
        assertFalse(player.isMoving());
        loop.render(0.2f);
        assertEquals(new GridPoint2(0, 0), player.getCoordinates());
        loop.render(0.2f);
        assertTrue(player.isMoving());
        assertEquals(new GridPoint2(1, 0), player.getDestinationCoordinates());
        loop.render(0.2f);
        assertEquals(new GridPoint2(1, 0), player.getCoordinates());
    }

    @Test
    public void playerAndAiCompetingForOneCellCannotBothStartMoving() {
        TankField field = new TankField(new Level(3, 1, new GridPoint2(0, 0), Collections.emptyList()));
        Tank player = field.createTank(new GridPoint2(0, 0), 0.4f, Direction.RIGHT);
        Tank bot = field.createTank(new GridPoint2(2, 0), 0.4f, Direction.LEFT);
        KeyboardHandler keyboard = new KeyboardHandler(key -> true);
        TankControls.bindMovement(keyboard, player::move, Collections.singletonMap(Direction.RIGHT, new int[]{1}));
        ControlStrategy ai = new RandomControlStrategy(bot::isMoving,
                Collections.singletonList(new MoveCommand(bot::move, Direction.LEFT)), new Random(1));
        loop(field, Arrays.asList(keyboard, ai)).render(0.2f);
        assertTrue(player.isMoving());
        assertFalse(bot.isMoving());
        assertEquals(new GridPoint2(1, 0), player.getDestinationCoordinates());
    }

    @Test
    public void manyFramesKeepAllTanksInsideBothLevelTypesWithoutOverlaps() throws IOException {
        List<Level> levels = Arrays.asList(LevelOptions.fromArguments(new String[0]).load(),
                new RandomLevelLoader(10, 8, 16, new Random(42)).load());
        for (Level level : levels) {
            TankField field = new TankField(level);
            Tank player = field.createTank(level.getPlayerStart(), 0.4f, Direction.RIGHT);
            List<Tank> bots = new RandomTankSpawner(level.getWidth(), level.getHeight(), 12, 0.4f, new Random(9))
                    .spawn(field);
            List<ControlStrategy> controls = new ArrayList<>();
            KeyboardHandler keyboard = new KeyboardHandler(key -> false);
            TankControls.bindMovement(keyboard, player::move, Collections.singletonMap(Direction.RIGHT, new int[]{1}));
            controls.add(keyboard);
            for (int i = 0; i < bots.size(); i++) {
                Tank bot = bots.get(i);
                List<Command> commands = new ArrayList<>();
                for (Direction direction : Direction.values()) {
                    commands.add(new MoveCommand(bot::move, direction));
                }
                controls.add(new RandomControlStrategy(bot::isMoving, commands, new Random(i)));
            }
            GameLoop loop = loop(field, controls);
            Set<GridPoint2> visited = new HashSet<>();
            for (int frame = 0; frame < 2000; frame++) {
                loop.render(frame % 7 == 0 ? 0.5f : 0.05f);
                Set<GridPoint2> occupied = new HashSet<>();
                for (Tank tank : field.getTanks()) {
                    GridPoint2 from = tank.getCoordinates();
                    GridPoint2 to = tank.getDestinationCoordinates();
                    assertTrue(level.isPassable(from));
                    assertTrue(level.isPassable(to));
                    assertTrue(occupied.add(from));
                    if (!from.equals(to)) {
                        assertTrue(occupied.add(to));
                    }
                    visited.add(from);
                }
                assertEquals(level.getPlayerStart(), player.getCoordinates());
                assertFalse(player.isMoving());
            }
            assertTrue(visited.size() > field.getTanks().size());
        }
    }

    private static GameLoop loop(TankField field, List<ControlStrategy> controls) {
        return new GameLoop(new CompositeControlStrategy(controls), new GameModel(field.getTanks()), new Renderer() {
            @Override
            public void render() {
            }

            @Override
            public void dispose() {
            }
        });
    }
}
