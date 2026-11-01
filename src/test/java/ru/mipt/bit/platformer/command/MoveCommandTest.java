package ru.mipt.bit.platformer.command;

import com.badlogic.gdx.math.GridPoint2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.TankField;

import static org.junit.Assert.*;

public class MoveCommandTest {
    @Test
    public void storesTheReceiverAndDirectionUntilExecution() {
        List<Direction> moves = new ArrayList<>();
        Command command = new MoveCommand(moves::add, Direction.LEFT);
        assertTrue(moves.isEmpty());
        command.execute();
        command.execute();
        assertEquals(Arrays.asList(Direction.LEFT, Direction.LEFT), moves);
    }

    @Test
    public void delegatesCollisionAndMovementRulesToTheReceiver() {
        Tank tank = new TankField(tile -> tile.x < 1).createTank(new GridPoint2(0, 0), 0.4f, Direction.UP);
        new MoveCommand(tank::move, Direction.RIGHT).execute();
        assertEquals(Direction.RIGHT, tank.getDirection());
        assertFalse(tank.isMoving());
        new MoveCommand(tank::move, Direction.UP).execute();
        new MoveCommand(tank::move, Direction.LEFT).execute();
        tank.update(0.4f);
        assertEquals(new GridPoint2(0, 1), tank.getCoordinates());
    }

    @Test
    public void rejectsMissingReceiverOrDirection() {
        assertThrows(NullPointerException.class, () -> new MoveCommand(null, Direction.UP));
        assertThrows(NullPointerException.class, () -> new MoveCommand(direction -> { }, null));
    }
}
