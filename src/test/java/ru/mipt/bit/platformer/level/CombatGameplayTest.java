package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.Test;
import ru.mipt.bit.platformer.GameLoop;
import ru.mipt.bit.platformer.Renderer;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.CommandQueue;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.command.ShootCommand;
import ru.mipt.bit.platformer.control.CompositeControlStrategy;
import ru.mipt.bit.platformer.control.ControlStrategy;
import ru.mipt.bit.platformer.control.RandomControlStrategy;
import ru.mipt.bit.platformer.model.*;

import static org.junit.Assert.*;

public class CombatGameplayTest {
    @Test
    public void longBattleKeepsModelAndObserverInSyncWithoutOverlappingTanks() {
        Level layout = new Level(8, 8, new GridPoint2(0, 0), List.of());
        GameModel model = new GameModel();
        TankField field = new TankField(layout, model, 25, new WeaponSettings(25, 0.1f, 0.25f));
        Set<Updatable> observed = new HashSet<>();
        int[] shotsAndDeaths = {0, 0};
        model.addListener(new ModelListener() {
            public void objectAdded(Updatable object) {
                assertTrue(observed.add(object));
                if (object instanceof Bullet) shotsAndDeaths[0]++;
            }
            public void objectRemoved(Updatable object) {
                assertTrue(observed.remove(object));
                if (object instanceof Tank) shotsAndDeaths[1]++;
            }
        });
        Random random = new Random(42);
        List<Tank> tanks = new RandomTankSpawner(8, 8, 12, 0.4f, random).spawn(field);
        CommandQueue queue = new CommandQueue();
        List<ControlStrategy> controls = new ArrayList<>();
        for (Tank tank : tanks) {
            List<Command> choices = new ArrayList<>();
            for (Direction direction : Direction.values()) {
                choices.add(queue.defer(new MoveCommand(tank::move, direction)));
            }
            choices.add(queue.defer(new ShootCommand(tank::shoot)));
            controls.add(new RandomControlStrategy(() -> !tank.isAlive() || tank.isMoving(), choices, random));
        }
        GameLoop loop = new GameLoop(new CompositeControlStrategy(controls), queue, model, new Renderer() {
            public void render() {
                assertEquals(new HashSet<>(model.getObjects()), observed);
            }
            public void dispose() { }
        });
        for (int frame = 0; frame < 3000; frame++) {
            loop.render(0.025f);
            Set<GridPoint2> occupied = new HashSet<>();
            for (Tank tank : field.getTanks()) {
                assertTrue(tank.isAlive());
                assertTrue(layout.isPassable(tank.getCoordinates()));
                assertTrue(layout.isPassable(tank.getDestinationCoordinates()));
                assertTrue(occupied.add(tank.getCoordinates()));
                if (tank.isMoving()) assertTrue(occupied.add(tank.getDestinationCoordinates()));
            }
            for (Updatable object : model.getObjects()) {
                if (object instanceof Bullet) assertTrue(((Bullet) object).isActive());
            }
        }
        assertTrue(shotsAndDeaths[0] > 10);
        assertTrue(shotsAndDeaths[1] > 0);
        assertEquals(tanks.size() - shotsAndDeaths[1], field.getTanks().size());
    }
}
