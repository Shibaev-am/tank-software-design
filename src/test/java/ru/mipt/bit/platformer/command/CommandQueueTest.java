package ru.mipt.bit.platformer.command;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.*;

public class CommandQueueTest {
    @Test
    public void defersCommandsAndExecutesThemOnceInOrder() {
        CommandQueue queue = new CommandQueue();
        List<Integer> calls = new ArrayList<>();
        queue.submit(() -> calls.add(1));
        Command selection = queue.defer(() -> calls.add(2));
        selection.execute();
        selection.execute();
        assertTrue(calls.isEmpty());
        queue.execute();
        queue.execute();
        assertEquals(List.of(1, 2, 2), calls);
    }

    @Test
    public void executesCommandsSubmittedDuringExecutionOnTheNextFrame() {
        CommandQueue queue = new CommandQueue();
        List<Integer> calls = new ArrayList<>();
        queue.submit(() -> {
            calls.add(1);
            queue.submit(() -> calls.add(2));
        });
        queue.execute();
        assertEquals(List.of(1), calls);
        queue.execute();
        assertEquals(List.of(1, 2), calls);
    }

    @Test
    public void rejectsMissingCommands() {
        CommandQueue queue = new CommandQueue();
        assertThrows(NullPointerException.class, () -> queue.submit(null));
        assertThrows(NullPointerException.class, () -> queue.defer(null));
    }
}
