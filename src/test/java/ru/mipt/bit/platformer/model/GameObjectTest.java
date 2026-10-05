package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GameObjectTest {
    @Test
    public void ownsItsPositionIndependentlyOfConstructorArgument() {
        GridPoint2 start = new GridPoint2(2, 3);
        GameObject object = new GameObject(start) { };
        start.set(0, 0);
        assertEquals(new GridPoint2(2, 3), object.getCoordinates());
    }

    @Test
    public void returnsACopyOfItsPosition() {
        GameObject object = new GameObject(new GridPoint2(2, 3)) { };
        object.getCoordinates().set(0, 0);
        assertEquals(new GridPoint2(2, 3), object.getCoordinates());
    }
}
