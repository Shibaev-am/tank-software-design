package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class GameModelTest {
    @Test
    public void updatesEveryInjectedObjectInOrder() {
        List<Float> deltas = new ArrayList<>();
        Updatable first = delta -> deltas.add(delta);
        Updatable second = delta -> deltas.add(delta * 2);
        GameModel model = new GameModel(Arrays.asList(first, second));
        model.update(0.2f);
        assertEquals(Arrays.asList(0.2f, 0.4f), deltas);
    }

    @Test
    public void updatesAnObjectAddedAfterConstruction() {
        int[] updates = {0};
        GameModel model = new GameModel(Collections.emptyList());
        model.update(1f);
        model.addObject(delta -> updates[0]++);
        model.update(1f);
        assertEquals(1, updates[0]);
    }

    @Test
    public void doesNotDependOnChangesToTheOriginalCollection() {
        int[] updates = {0};
        List<Updatable> objects = new ArrayList<>();
        objects.add(delta -> updates[0]++);
        GameModel model = new GameModel(objects);
        objects.clear();
        model.update(1f);
        assertEquals(1, updates[0]);
    }

    @Test
    public void updatesATankUsingTheInjectedCollisionPolicy() {
        Tank tank = new Tank(new GridPoint2(5, 5), tile -> tile.y <= 6, 0.2f, Direction.LEFT);
        GameModel model = new GameModel(Collections.singletonList(tank));
        tank.move(Direction.UP);
        model.update(0.2f);
        assertEquals(new GridPoint2(5, 6), tank.getCoordinates());
        tank.move(Direction.UP);
        model.update(0.2f);
        assertEquals(new GridPoint2(5, 6), tank.getCoordinates());
        assertFalse(tank.isMoving());
    }

    @Test(expected = NullPointerException.class)
    public void rejectsNullObjects() {
        new GameModel(Collections.emptyList()).addObject(null);
    }
}
