package ru.mipt.bit.platformer.command;

import org.junit.Test;

import static org.junit.Assert.*;

public class ShootCommandTest {
    @Test
    public void callsTheSelectedReceiverOnlyWhenExecuted() {
        int[] shots = {0};
        ShootCommand command = new ShootCommand(() -> shots[0]++);
        assertEquals(0, shots[0]);
        command.execute();
        command.execute();
        assertEquals(2, shots[0]);
    }

    @Test
    public void requiresAReceiver() {
        assertThrows(NullPointerException.class, () -> new ShootCommand(null));
    }
}
