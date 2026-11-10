package ru.mipt.bit.platformer.command;

import org.junit.Test;
import ru.mipt.bit.platformer.input.ToggleState;

import static org.junit.Assert.*;

public class ToggleCommandTest {
    @Test
    public void changesStateOnlyWhenExecuted() {
        ToggleState state = new ToggleState(false);
        ToggleCommand command = new ToggleCommand(state::toggle);
        assertFalse(state.getAsBoolean());
        command.execute();
        assertTrue(state.getAsBoolean());
        command.execute();
        assertFalse(state.getAsBoolean());
    }

    @Test
    public void canStartEnabledAndOperateOnAnotherReceiver() {
        ToggleState state = new ToggleState(true);
        int[] calls = {0};
        new ToggleCommand(() -> {
            calls[0]++;
            state.toggle();
        }).execute();
        assertEquals(1, calls[0]);
        assertFalse(state.getAsBoolean());
    }

    @Test
    public void rejectsAMissingReceiver() {
        assertThrows(NullPointerException.class, () -> new ToggleCommand(null));
    }
}
