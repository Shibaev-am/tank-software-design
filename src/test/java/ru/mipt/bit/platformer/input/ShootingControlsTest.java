package ru.mipt.bit.platformer.input;

import com.badlogic.gdx.math.GridPoint2;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.Test;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.CommandQueue;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.command.ShootCommand;
import ru.mipt.bit.platformer.control.RandomControlStrategy;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.TankField;

import static com.badlogic.gdx.Input.Keys.SPACE;
import static org.junit.Assert.*;

public class ShootingControlsTest {
    @Test
    public void spaceQueuesOneShotPerPress() {
        boolean[] pressed = {false};
        int[] shots = {0};
        KeyboardHandler keyboard = new KeyboardHandler(key -> key == SPACE && pressed[0]);
        CommandQueue queue = new CommandQueue();
        keyboard.bindPressed(queue.defer(new ShootCommand(() -> shots[0]++)), SPACE);
        keyboard.update();
        pressed[0] = true;
        keyboard.update();
        keyboard.update();
        assertEquals(0, shots[0]);
        queue.execute();
        assertEquals(1, shots[0]);
        pressed[0] = false;
        keyboard.update();
        pressed[0] = true;
        keyboard.update();
        queue.execute();
        assertEquals(2, shots[0]);
    }

    @Test
    public void randomAiSelectsShootAlongsideTheFourMovementCommands() {
        List<String> actions = new ArrayList<>();
        CommandQueue queue = new CommandQueue();
        List<Command> choices = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            choices.add(queue.defer(new MoveCommand(value -> actions.add(value.name()), direction)));
        }
        choices.add(queue.defer(new ShootCommand(() -> actions.add("SHOOT"))));
        Random selection = new Random(0) {
            private int index;
            public int nextInt(int bound) {
                assertEquals(5, bound);
                return index++ % bound;
            }
        };
        RandomControlStrategy ai = new RandomControlStrategy(() -> false, choices, selection);
        for (int i = 0; i < 5; i++) {
            ai.update();
        }
        assertTrue(actions.isEmpty());
        queue.execute();
        assertEquals(List.of("UP", "LEFT", "DOWN", "RIGHT", "SHOOT"), actions);
    }

    @Test
    public void commandsSelectedBeforeDeathCannotMoveOrShootAfterDeath() {
        TankField field = new TankField(tile -> true);
        Tank tank = field.createTank(new GridPoint2(0, 0), 0.4f, Direction.RIGHT);
        CommandQueue queue = new CommandQueue();
        queue.submit(() -> tank.takeDamage(100));
        queue.submit(new ShootCommand(tank::shoot));
        queue.submit(new MoveCommand(tank::move, Direction.UP));
        queue.execute();
        assertTrue(field.getModel().getObjects().isEmpty());
        assertEquals(new GridPoint2(0, 0), tank.getCoordinates());
        assertFalse(tank.isMoving());
    }
}
