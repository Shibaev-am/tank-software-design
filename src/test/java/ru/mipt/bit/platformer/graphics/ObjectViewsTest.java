package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.GridPoint2;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import ru.mipt.bit.platformer.model.GameModel;
import ru.mipt.bit.platformer.model.Tree;
import ru.mipt.bit.platformer.model.Updatable;

import static org.junit.Assert.*;

public class ObjectViewsTest {
    @Test
    public void createsRendersAndDisposesViewsThroughModelEvents() {
        Tree first = new Tree(new GridPoint2());
        GameModel model = new GameModel(List.of(first));
        Map<Updatable, FakeView> created = new IdentityHashMap<>();
        ObjectViews views = new ObjectViews(model, object -> {
            FakeView view = new FakeView();
            created.put(object, view);
            return view;
        });
        Tree second = new Tree(new GridPoint2(1, 0));
        model.addObject(second);
        views.render(null);
        assertEquals(1, created.get(first).renders);
        assertEquals(1, created.get(second).renders);
        model.removeObject(first);
        model.removeObject(first);
        assertEquals(1, created.get(first).disposals);
        views.render(null);
        assertEquals(1, created.get(first).renders);
        assertEquals(2, created.get(second).renders);
        views.dispose();
        views.dispose();
        assertEquals(1, created.get(second).disposals);
        model.addObject(new Tree(new GridPoint2(2, 0)));
        assertEquals(2, created.size());
    }

    @Test
    public void factoriesCanBeExtendedWithoutChangingTheObserver() {
        ViewFactory factory = new ViewFactory();
        FakeView treeView = new FakeView();
        factory.register(Tree.class, tree -> treeView);
        assertSame(treeView, factory.apply(new Tree(new GridPoint2())));
        class AdditionalObject implements Updatable {
            public void update(float delta) { }
        }
        AdditionalObject additional = new AdditionalObject();
        assertThrows(IllegalArgumentException.class, () -> factory.apply(additional));
        FakeView additionalView = new FakeView();
        factory.register(AdditionalObject.class, object -> additionalView);
        assertSame(additionalView, factory.apply(additional));
        assertThrows(NullPointerException.class, () -> factory.register(Tree.class, null));
    }

    private static class FakeView implements SpriteRenderer {
        private int renders;
        private int disposals;
        public void render(Batch batch) {
            renders++;
        }
        public void dispose() {
            disposals++;
        }
    }
}
