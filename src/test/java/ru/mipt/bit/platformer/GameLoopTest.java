package ru.mipt.bit.platformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import ru.mipt.bit.platformer.command.CommandQueue;

import static org.junit.Assert.*;

public class GameLoopTest {
    @Test
    public void processesInputBeforeModelAndRendersUpdatedState() {
        List<String> calls = new ArrayList<>();
        float[] delta = {0f};
        CommandQueue commands = new CommandQueue();
        GameLoop loop = new GameLoop(() -> {
            calls.add("input");
            commands.submit(() -> calls.add("command"));
        }, commands, time -> {
            delta[0] = time;
            calls.add("model");
        }, renderer(calls));
        loop.render(0.25f);
        assertEquals(Arrays.asList("input", "command", "model", "render"), calls);
        assertEquals(0.25f, delta[0], 0f);
    }

    @Test
    public void disposesTheInjectedRenderer() {
        List<String> calls = new ArrayList<>();
        GameLoop loop = new GameLoop(() -> { }, new CommandQueue(), delta -> { }, renderer(calls));
        loop.dispose();
        assertEquals(Arrays.asList("dispose"), calls);
    }

    @Test(expected = NullPointerException.class)
    public void requiresAControlStrategy() {
        new GameLoop(null, new CommandQueue(), delta -> { }, renderer(new ArrayList<>()));
    }

    @Test(expected = NullPointerException.class)
    public void requiresACommandExecutor() {
        new GameLoop(() -> { }, null, delta -> { }, renderer(new ArrayList<>()));
    }

    @Test(expected = NullPointerException.class)
    public void requiresAModel() {
        new GameLoop(() -> { }, new CommandQueue(), null, renderer(new ArrayList<>()));
    }

    @Test(expected = NullPointerException.class)
    public void requiresARenderer() {
        new GameLoop(() -> { }, new CommandQueue(), delta -> { }, null);
    }

    private static Renderer renderer(List<String> calls) {
        return new Renderer() {
            @Override
            public void render() {
                calls.add("render");
            }

            @Override
            public void dispose() {
                calls.add("dispose");
            }
        };
    }
}
