package ru.mipt.bit.platformer.control;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class CompositeControlStrategyTest {
    @Test
    public void updatesEveryStrategyInOrderOnEveryFrame() {
        List<String> calls = new ArrayList<>();
        ControlStrategy controls = new CompositeControlStrategy(Arrays.asList(
                () -> calls.add("player"), () -> calls.add("bot")));
        controls.update();
        controls.update();
        assertEquals(Arrays.asList("player", "bot", "player", "bot"), calls);
    }

    @Test
    public void keepsItsOwnListOfStrategies() {
        int[] calls = {0};
        List<ControlStrategy> source = new ArrayList<>();
        source.add(() -> calls[0]++);
        CompositeControlStrategy controls = new CompositeControlStrategy(source);
        source.clear();
        controls.update();
        assertEquals(1, calls[0]);
    }

    @Test
    public void acceptsAnEmptyGroupAndRejectsNullStrategies() {
        new CompositeControlStrategy(Collections.emptyList()).update();
        assertThrows(NullPointerException.class, () -> new CompositeControlStrategy(null));
        assertThrows(NullPointerException.class,
                () -> new CompositeControlStrategy(Collections.singletonList(null)));
    }
}
