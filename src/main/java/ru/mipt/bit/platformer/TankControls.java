package ru.mipt.bit.platformer;

import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Tank;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.IntPredicate;

import static com.badlogic.gdx.Input.Keys.*;

public class TankControls {
    private final Map<Direction, int[]> bindings = new EnumMap<>(Direction.class);
    private final IntPredicate isKeyPressed;

    public TankControls(IntPredicate isKeyPressed) {
        this.isKeyPressed = isKeyPressed;
        bindings.put(Direction.UP, new int[]{UP, W});
        bindings.put(Direction.LEFT, new int[]{LEFT, A});
        bindings.put(Direction.DOWN, new int[]{DOWN, S});
        bindings.put(Direction.RIGHT, new int[]{RIGHT, D});
    }

    public void update(Tank tank) {
        for (Direction direction : Direction.values()) {
            for (int key : bindings.get(direction)) {
                if (isKeyPressed.test(key)) {
                    tank.move(direction);
                    break;
                }
            }
        }
    }
}
