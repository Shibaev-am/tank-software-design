package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.input.ToggleState;
import ru.mipt.bit.platformer.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class HealthBarDecoratorTest {
    @Test
    public void keepsTheObjectVisibleAndDrawsTheEnabledBarAfterIt() {
        List<String> calls = new ArrayList<>();
        ToggleState visible = new ToggleState(false);
        SpriteRenderer view = new HealthBarDecorator(view("tank", calls), view("health", calls), visible);
        view.render(null);
        assertEquals(List.of("tank"), calls);
        calls.clear();
        visible.toggle();
        view.render(null);
        assertEquals(List.of("tank", "health"), calls);
        calls.clear();
        visible.toggle();
        view.render(null);
        assertEquals(List.of("tank"), calls);
    }

    @Test
    public void existingAndNewObjectsUseTheSameSwitch() {
        List<String> calls = new ArrayList<>();
        ToggleState visible = new ToggleState(false);
        GameModel model = new GameModel();
        ObjectViews views = new ObjectViews(model, object -> new HealthBarDecorator(
                view("object", calls), view("health", calls), visible));
        TankField field = new TankField(tile -> true, model, 100, new WeaponSettings(25, 0.1f, 0.5f));
        Tank tank = field.createTank(new GridPoint2(), 0.4f, Direction.RIGHT);
        views.render(null);
        assertEquals(List.of("object"), calls);
        calls.clear();
        visible.toggle();
        tank.shoot();
        views.render(null);
        assertEquals(List.of("object", "health", "object", "health"), calls);
        calls.clear();
        visible.toggle();
        views.render(null);
        assertEquals(List.of("object", "object"), calls);
        calls.clear();
        Bullet bullet = (Bullet) model.getObjects().get(1);
        bullet.destroy();
        assertEquals(List.of("dispose object", "dispose health"), calls);
        calls.clear();
        tank.takeDamage(100);
        assertEquals(List.of("dispose object", "dispose health"), calls);
        calls.clear();
        views.dispose();
        assertTrue(calls.isEmpty());
    }

    @Test
    public void disposesTheHiddenBarTogetherWithTheObject() {
        List<String> calls = new ArrayList<>();
        new HealthBarDecorator(view("tank", calls), view("health", calls), () -> false).dispose();
        assertEquals(List.of("dispose tank", "dispose health"), calls);
    }

    @Test
    public void releasesTheBarEvenWhenTheWrappedObjectFailsToDispose() {
        List<String> calls = new ArrayList<>();
        SpriteRenderer broken = new SpriteRenderer() {
            public void render(Batch batch) { }
            public void dispose() { throw new IllegalStateException("Cannot dispose"); }
        };
        SpriteRenderer decorated = new HealthBarDecorator(broken, view("health", calls), () -> true);
        assertThrows(IllegalStateException.class, decorated::dispose);
        assertEquals(List.of("dispose health"), calls);
    }

    private static SpriteRenderer view(String name, List<String> calls) {
        return new SpriteRenderer() {
            public void render(Batch batch) { calls.add(name); }
            public void dispose() { calls.add("dispose " + name); }
        };
    }
}
