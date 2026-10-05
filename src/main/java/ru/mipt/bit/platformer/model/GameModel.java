package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public class GameModel {
    private final Field field;
    private final Tank tank;

    public GameModel() {
        field = new Field();
        field.addTree(new Tree(new GridPoint2(1, 3)));
        tank = new Tank(new GridPoint2(1, 1), field);
    }

    public void update(float deltaTime) {
        tank.update(deltaTime);
    }

    public Field getField() {
        return field;
    }

    public Tank getTank() {
        return tank;
    }
}
