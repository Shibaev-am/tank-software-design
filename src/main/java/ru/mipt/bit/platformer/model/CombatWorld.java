package ru.mipt.bit.platformer.model;

public interface CombatWorld {
    void addBullet(Bullet bullet);
    void remove(GameObject object);
    boolean collide(Bullet bullet);
    void collide(Tank tank);
}
