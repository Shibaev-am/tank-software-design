package ru.mipt.bit.platformer.input;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Before;
import org.junit.Test;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import java.util.HashSet;
import java.util.Set;

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
        tank = new Tank(new GridPoint2(1, 1), field);
        TankControls.bindMovement(keyboard, tank);
    }

    @Test
    public void arrowAndLetterBindingsMoveInTheSameDirection() {
        int[][] keys = {{UP, W}, {LEFT, A}, {DOWN, S}, {RIGHT, D}};
        Direction[] directions = Direction.values();
        for (int i = 0; i < keys.length; i++) {
            for (int key : keys[i]) {
                pressedKeys.clear();
                pressedKeys.add(key);
                Tank player = new Tank(new GridPoint2(3, 3), field);
                KeyboardHandler input = new KeyboardHandler(pressedKeys::contains);
                TankControls.bindMovement(input, player);
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
        field.addTree(new Tree(new GridPoint2(1, 2)));
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
}
