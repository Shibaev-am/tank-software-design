package ru.mipt.bit.platformer.control;

import com.badlogic.gdx.math.GridPoint2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.junit.Test;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.TankField;

import static org.junit.Assert.*;

public class RandomControlStrategyTest {
    @Test
    public void executesOneSelectedCommandPerIdleFrame() {
        List<Integer> calls = new ArrayList<>();
        Random choices = new Random(12);
        RandomControlStrategy control = new RandomControlStrategy(() -> false,
                Arrays.asList(() -> calls.add(0), () -> calls.add(1), () -> calls.add(2)), new Random(12));
        for (int frame = 0; frame < 20; frame++) {
            control.update();
            assertEquals(frame + 1, calls.size());
            assertEquals(choices.nextInt(3), calls.get(frame).intValue());
        }
    }

    @Test
    public void leavesAnActiveMoveAloneAndContinuesAfterArrival() {
        Tank tank = new TankField(tile -> true).createTank(new GridPoint2(1, 1), 0.4f, Direction.UP);
        RandomControlStrategy control = new RandomControlStrategy(tank::isMoving,
                Collections.singletonList(new MoveCommand(tank::move, Direction.RIGHT)), new Random(1));
        control.update();
        tank.update(0.2f);
        control.update();
        assertEquals(new GridPoint2(1, 1), tank.getCoordinates());
        assertEquals(0.5f, tank.getMovementProgress(), 0f);
        tank.update(0.2f);
        control.update();
        assertEquals(new GridPoint2(3, 1), tank.getDestinationCoordinates());
        assertEquals(0f, tank.getMovementProgress(), 0f);
    }

    @Test
    public void doesNotChooseOrExecuteCommandsWhileBusy() {
        boolean[] busy = {true};
        int[] calls = {0};
        Random random = new Random(1) {
            @Override
            public int nextInt(int bound) {
                assertFalse(busy[0]);
                return 0;
            }
        };
        RandomControlStrategy control = new RandomControlStrategy(() -> busy[0],
                Collections.singletonList(() -> calls[0]++), random);
        control.update();
        control.update();
        assertEquals(0, calls[0]);
        busy[0] = false;
        control.update();
        assertEquals(1, calls[0]);
    }

    @Test
    public void retriesAfterABlockedMove() {
        Tank tank = new TankField(tile -> tile.x == 0).createTank(new GridPoint2(0, 0), 0.4f, Direction.UP);
        Random sequence = new Random(0) {
            private int next;
            @Override
            public int nextInt(int bound) {
                return next++;
            }
        };
        RandomControlStrategy control = new RandomControlStrategy(tank::isMoving, Arrays.asList(
                new MoveCommand(tank::move, Direction.RIGHT), new MoveCommand(tank::move, Direction.UP)), sequence);
        control.update();
        assertFalse(tank.isMoving());
        control.update();
        assertTrue(tank.isMoving());
        assertEquals(new GridPoint2(0, 1), tank.getDestinationCoordinates());
    }

    @Test
    public void copiesCommandsAndValidatesDependencies() {
        int[] calls = {0};
        List<Command> commands = new ArrayList<>();
        commands.add(() -> calls[0]++);
        RandomControlStrategy control = new RandomControlStrategy(() -> false, commands, new Random(0));
        commands.clear();
        control.update();
        assertEquals(1, calls[0]);
        assertThrows(IllegalArgumentException.class,
                () -> new RandomControlStrategy(() -> false, commands, new Random()));
        assertThrows(NullPointerException.class,
                () -> new RandomControlStrategy(null, Collections.singletonList(() -> { }), new Random()));
        assertThrows(NullPointerException.class,
                () -> new RandomControlStrategy(() -> false, null, new Random()));
        assertThrows(NullPointerException.class,
                () -> new RandomControlStrategy(() -> false, Collections.singletonList(null), new Random()));
        assertThrows(NullPointerException.class,
                () -> new RandomControlStrategy(() -> false, Collections.singletonList(() -> { }), null));
    }
}
