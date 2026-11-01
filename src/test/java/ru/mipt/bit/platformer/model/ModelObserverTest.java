package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.*;

public class ModelObserverTest {
    @Test
    public void replaysInitialObjectsAndReportsLaterChangesExactlyOnce() {
        Updatable tree = new Tree(new GridPoint2(1, 1));
        GameModel model = new GameModel(List.of(tree));
        List<Updatable> added = new ArrayList<>();
        List<Updatable> removed = new ArrayList<>();
        ModelListener listener = listener(added, removed);
        model.addListener(listener);
        model.addListener(listener);
        Updatable object = delta -> { };
        model.addObject(object);
        model.addObject(object);
        model.removeObject(tree);
        model.removeObject(tree);
        assertEquals(List.of(tree, object), added);
        assertEquals(List.of(tree), removed);
        assertEquals(List.of(object), model.getObjects());
        assertThrows(UnsupportedOperationException.class, () -> model.getObjects().clear());
    }

    @Test
    public void listenerSubscribedBeforePopulationSeesEveryInitialObject() {
        GameModel model = new GameModel();
        List<Updatable> added = new ArrayList<>();
        List<Updatable> removed = new ArrayList<>();
        model.addListener(listener(added, removed));
        Tree tree = new Tree(new GridPoint2(1, 1));
        model.addObject(tree);
        TankField field = new TankField(tile -> true, model, 100, new WeaponSettings(25, 1f, 1f));
        Tank tank = field.createTank(new GridPoint2(0, 0), 0.4f, Direction.RIGHT);
        tank.shoot();
        assertEquals(model.getObjects(), added);
        assertEquals(3, added.size());
        assertTrue(removed.isEmpty());
    }

    @Test
    public void removedObjectsAreSkippedAndNewObjectsWaitForNextUpdate() {
        GameModel model = new GameModel();
        List<String> calls = new ArrayList<>();
        Updatable victim = delta -> calls.add("victim");
        Updatable newcomer = delta -> calls.add("new");
        Updatable first = delta -> {
            calls.add("first");
            model.removeObject(victim);
            model.addObject(newcomer);
        };
        model.addObject(first);
        model.addObject(victim);
        model.update(1f);
        assertEquals(List.of("first"), calls);
        model.update(1f);
        assertEquals(List.of("first", "first", "new"), calls);
    }

    @Test
    public void subscriberCanUnsubscribeDuringNotification() {
        GameModel model = new GameModel();
        int[] notifications = {0};
        ModelListener once = new ModelListener() {
            public void objectAdded(Updatable object) {
                notifications[0]++;
                model.removeListener(this);
            }
            public void objectRemoved(Updatable object) {
                fail("Unsubscribed listener called");
            }
        };
        model.addListener(once);
        Tree first = new Tree(new GridPoint2());
        model.addObject(first);
        model.addObject(new Tree(new GridPoint2(1, 0)));
        model.removeObject(first);
        assertEquals(1, notifications[0]);
    }

    @Test
    public void rejectsMissingListener() {
        assertThrows(NullPointerException.class, () -> new GameModel().addListener(null));
    }

    private static ModelListener listener(List<Updatable> added, List<Updatable> removed) {
        return new ModelListener() {
            public void objectAdded(Updatable object) {
                added.add(object);
            }
            public void objectRemoved(Updatable object) {
                removed.add(object);
            }
        };
    }
}
