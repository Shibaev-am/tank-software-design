package ru.mipt.bit.platformer.input;

import org.junit.Test;
import ru.mipt.bit.platformer.command.CommandQueue;
import ru.mipt.bit.platformer.command.ToggleCommand;

import static com.badlogic.gdx.Input.Keys.*;
import static org.junit.Assert.*;

public class HealthBarControlsTest {
    @Test
    public void holdingLChangesVisibilityOnceAndAnotherPressChangesItBack() {
        boolean[] pressed = {false};
        ToggleState visible = new ToggleState(false);
        KeyboardHandler keyboard = new KeyboardHandler(key -> key == L && pressed[0]);
        CommandQueue queue = new CommandQueue();
        keyboard.bindPressed(queue.defer(new ToggleCommand(visible::toggle)), L);
        keyboard.update();
        queue.execute();
        assertFalse(visible.getAsBoolean());
        pressed[0] = true;
        keyboard.update();
        assertFalse(visible.getAsBoolean());
        queue.execute();
        assertTrue(visible.getAsBoolean());
        for (int frame = 0; frame < 10; frame++) {
            keyboard.update();
            queue.execute();
            assertTrue(visible.getAsBoolean());
        }
        pressed[0] = false;
        keyboard.update();
        queue.execute();
        assertTrue(visible.getAsBoolean());
        pressed[0] = true;
        keyboard.update();
        queue.execute();
        assertFalse(visible.getAsBoolean());
    }

    @Test
    public void togglingDoesNotReplaceMovementOrShootingBindings() {
        ToggleState visible = new ToggleState(false);
        KeyboardHandler keyboard = new KeyboardHandler(key -> key == L || key == SPACE || key == RIGHT);
        int[] calls = {0, 0};
        keyboard.bindPressed(new ToggleCommand(visible::toggle), L);
        keyboard.bindPressed(() -> calls[0]++, SPACE);
        keyboard.bindHeld(() -> calls[1]++, RIGHT);
        keyboard.update();
        keyboard.update();
        assertTrue(visible.getAsBoolean());
        assertArrayEquals(new int[]{1, 2}, calls);
    }
}
