package ru.mipt.bit.platformer.input;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class KeyboardHandlerTest {
    private final Set<Integer> pressed = new HashSet<>();
    private final KeyboardHandler keyboard = new KeyboardHandler(pressed::contains);

    @Test
    public void unpressedAndUnboundKeysDoNotRunActions() {
        int[] calls = {0};
        keyboard.bindHeld(() -> calls[0]++, 1);
        keyboard.update();
        pressed.add(2);
        keyboard.update();
        assertEquals(0, calls[0]);
    }

    @Test
    public void heldActionRunsEveryFrameUntilRelease() {
        int[] calls = {0};
        keyboard.bindHeld(() -> calls[0]++, 1);
        pressed.add(1);
        keyboard.update();
        keyboard.update();
        pressed.clear();
        keyboard.update();
        assertEquals(2, calls[0]);
    }

    @Test
    public void eitherAliasRunsTheActionOnlyOncePerFrame() {
        int[] calls = {0};
        keyboard.bindHeld(() -> calls[0]++, 1, 2);
        pressed.add(2);
        keyboard.update();
        pressed.add(1);
        keyboard.update();
        assertEquals(2, calls[0]);
    }

    @Test
    public void pressedActionRunsOnceAndCanRunAgainAfterRelease() {
        int[] calls = {0};
        keyboard.bindPressed(() -> calls[0]++, 1);
        pressed.add(1);
        keyboard.update();
        keyboard.update();
        assertEquals(1, calls[0]);
        pressed.clear();
        keyboard.update();
        pressed.add(1);
        keyboard.update();
        assertEquals(2, calls[0]);
    }

    @Test
    public void changingHeldAliasDoesNotRetriggerPressedAction() {
        int[] calls = {0};
        keyboard.bindPressed(() -> calls[0]++, 1, 2);
        pressed.add(1);
        keyboard.update();
        pressed.add(2);
        pressed.remove(1);
        keyboard.update();
        assertEquals(1, calls[0]);
    }

    @Test
    public void actionsRunInRegistrationOrder() {
        List<Integer> calls = new ArrayList<>();
        keyboard.bindHeld(() -> calls.add(1), 1);
        keyboard.bindPressed(() -> calls.add(2), 2);
        pressed.addAll(Arrays.asList(1, 2));
        keyboard.update();
        assertEquals(Arrays.asList(1, 2), calls);
    }

    @Test
    public void newBindingsCanBeAddedWithoutChangingExistingOnes() {
        int[] calls = {0, 0};
        keyboard.bindHeld(() -> calls[0]++, 1);
        pressed.add(1);
        keyboard.update();
        keyboard.bindPressed(() -> calls[1]++, 2);
        pressed.add(2);
        keyboard.update();
        assertArrayEquals(new int[]{2, 1}, calls);
    }

    @Test
    public void bindingOwnsACopyOfItsKeys() {
        int[] keys = {1};
        int[] calls = {0};
        keyboard.bindHeld(() -> calls[0]++, keys);
        keys[0] = 2;
        pressed.add(1);
        keyboard.update();
        assertEquals(1, calls[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsBindingsWithoutKeys() {
        keyboard.bindHeld(() -> { });
    }

    @Test(expected = NullPointerException.class)
    public void rejectsMissingActions() {
        keyboard.bindPressed(null, 1);
    }

    @Test(expected = NullPointerException.class)
    public void rejectsMissingKeyStateSource() {
        new KeyboardHandler(null);
    }
}
