package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.GameLoop;
import ru.mipt.bit.platformer.Renderer;
import ru.mipt.bit.platformer.input.KeyboardHandler;
import ru.mipt.bit.platformer.input.TankControls;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameModel;
import ru.mipt.bit.platformer.model.Tank;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class LevelGameplayTest {
    @Test
    public void usesLoadedTreesAndBordersDuringTheGameLoop() throws IOException {
        Level level = new FileLevelLoader(() -> new ByteArrayInputStream(
                "_T_\n_X_\n___".getBytes(StandardCharsets.UTF_8))).load();
        Tank tank = new Tank(level.getPlayerStart(), level, 0.4f, Direction.RIGHT);
        Set<Integer> pressed = new HashSet<>();
        KeyboardHandler keyboard = new KeyboardHandler(pressed::contains);
        TankControls.bindMovement(keyboard, tank::move,
                Collections.singletonMap(Direction.UP, new int[]{1}));
        TankControls.bindMovement(keyboard, tank::move,
                Collections.singletonMap(Direction.RIGHT, new int[]{2}));
        int[] frames = {0};
        Renderer renderer = new Renderer() {
            @Override
            public void render() {
                frames[0]++;
            }

            @Override
            public void dispose() {
            }
        };
        GameLoop loop = new GameLoop(keyboard, new GameModel(Collections.singletonList(tank)), renderer);
        pressed.add(1);
        loop.render(0.4f);
        assertEquals(new GridPoint2(1, 1), tank.getCoordinates());
        pressed.clear();
        pressed.add(2);
        loop.render(0.4f);
        assertEquals(new GridPoint2(2, 1), tank.getCoordinates());
        loop.render(0.4f);
        assertEquals(new GridPoint2(2, 1), tank.getCoordinates());
        assertFalse(tank.isMoving());
        assertEquals(3, frames[0]);
    }
}
