package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

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
        Tank tank = new TankField(tile -> tile.y <= 6).createTank(new GridPoint2(5, 5), 0.2f, Direction.LEFT);
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

    @Test
    public void subdividesLongFramesWithoutLosingTime() {
        GameModel model = new GameModel(0.05f);
        List<Float> deltas = new ArrayList<>();
        model.addObject(deltas::add);
        model.update(0.13f);
        assertEquals(3, deltas.size());
        assertEquals(0.05f, deltas.get(0), 0.0001f);
        assertEquals(0.05f, deltas.get(1), 0.0001f);
        assertEquals(0.03f, deltas.get(2), 0.0001f);
        model.update(0f);
        assertEquals(0f, deltas.get(3), 0f);
    }

    @Test
    public void validatesTheMaximumUpdateStep() {
        for (float step : new float[]{0f, -1f, Float.NaN, Float.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> new GameModel(step));
        }
    }
}
