package ru.mipt.bit.platformer.input;

import com.badlogic.gdx.math.GridPoint2;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.TankField;
import ru.mipt.bit.platformer.model.Tree;

import static com.badlogic.gdx.Input.Keys.*;
import static org.junit.Assert.*;

public class TankControlsTest {
    private Field field;
    private Tank tank;
    private final Set<Integer> pressedKeys = new HashSet<>();
    private final KeyboardHandler keyboard = new KeyboardHandler(pressedKeys::contains);

    @Before
    public void setUp() {
        field = new Field();
        tank = new TankField(field).createTank(new GridPoint2(1, 1), 0.4f, Direction.RIGHT);
        TankControls.bindMovement(keyboard, tank::move, defaultKeys());
    }

    @Test
    public void arrowAndLetterBindingsMoveInTheSameDirection() {
        int[][] keys = {{UP, W}, {LEFT, A}, {DOWN, S}, {RIGHT, D}};
        Direction[] directions = Direction.values();
        for (int i = 0; i < keys.length; i++) {
            for (int key : keys[i]) {
                pressedKeys.clear();
                pressedKeys.add(key);
                Tank player = new TankField(field).createTank(new GridPoint2(3, 3), 0.4f, Direction.RIGHT);
                KeyboardHandler input = new KeyboardHandler(pressedKeys::contains);
                TankControls.bindMovement(input, player::move, defaultKeys());
                input.update();
                player.update(0.4f);
                assertEquals(directions[i].nextTile(new GridPoint2(3, 3)), player.getCoordinates());
            }
        }
    }

    @Test
    public void noPressedKeysLeaveTankIdle() {
        keyboard.update();
        assertFalse(tank.isMoving());
        assertEquals(Direction.RIGHT, tank.getDirection());
    }

    @Test
    public void simultaneousKeysUseOriginalPriority() {
        pressedKeys.add(UP);
        pressedKeys.add(LEFT);
        pressedKeys.add(DOWN);
        pressedKeys.add(RIGHT);
        keyboard.update();
        tank.update(0.4f);
        assertEquals(new GridPoint2(1, 2), tank.getCoordinates());
    }

    @Test
    public void blockedDirectionAllowsNextPressedDirection() {
        field.addObstacle(new Tree(new GridPoint2(1, 2)));
        pressedKeys.add(UP);
        pressedKeys.add(RIGHT);
        keyboard.update();
        tank.update(0.4f);
        assertEquals(new GridPoint2(2, 1), tank.getCoordinates());
        assertEquals(Direction.RIGHT, tank.getDirection());
    }

    @Test
    public void holdingBothBindingsDoesNotMoveTwice() {
        pressedKeys.add(UP);
        pressedKeys.add(W);
        keyboard.update();
        tank.update(0.4f);
        assertEquals(new GridPoint2(1, 2), tank.getCoordinates());
    }
    @Test
    public void extraActionCanRunWhileTankIsMoving() {
        int[] shots = {0};
        keyboard.bindPressed(() -> shots[0]++, SPACE);
        pressedKeys.add(UP);
        keyboard.update();
        tank.update(0.1f);
        pressedKeys.add(SPACE);
        keyboard.update();
        keyboard.update();
        assertEquals(1, shots[0]);
        assertEquals(Direction.UP, tank.getDirection());
        assertTrue(tank.isMoving());
    }
    private static Map<Direction, int[]> defaultKeys() {
        Map<Direction, int[]> keys = new EnumMap<>(Direction.class);
        keys.put(Direction.UP, new int[]{UP, W});
        keys.put(Direction.LEFT, new int[]{LEFT, A});
        keys.put(Direction.DOWN, new int[]{DOWN, S});
        keys.put(Direction.RIGHT, new int[]{RIGHT, D});
        return keys;
    }

    @Test
    public void customKeysControlAnArbitraryMovementAction() {
        List<Direction> moves = new ArrayList<>();
        Map<Direction, int[]> keys = new LinkedHashMap<>();
        keys.put(Direction.RIGHT, new int[]{77});
        keys.put(Direction.UP, new int[]{78});
        TankControls.bindMovement(keyboard, moves::add, keys);
        pressedKeys.add(77);
        pressedKeys.add(78);
        keyboard.update();
        assertEquals(java.util.Arrays.asList(Direction.RIGHT, Direction.UP), moves);
    }

    @Test
    public void bindingsCanBeRegisteredThroughAnotherImplementation() {
        List<Command> actions = new ArrayList<>();
        List<Integer> keys = new ArrayList<>();
        KeyBindings binder = new KeyBindings() {
            @Override
            public void bindHeld(Command action, int... aliases) {
                actions.add(action);
                keys.add(aliases[0]);
            }

            @Override
            public void bindPressed(Command action, int... aliases) {
                fail("Movement should use held bindings");
            }
        };
        List<Direction> moves = new ArrayList<>();
        TankControls.bindMovement(binder, moves::add,
                java.util.Collections.singletonMap(Direction.DOWN, new int[]{42}));
        assertEquals(java.util.Collections.singletonList(42), keys);
        actions.get(0).execute();
        assertEquals(java.util.Collections.singletonList(Direction.DOWN), moves);
    }
}
