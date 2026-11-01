package ru.mipt.bit.platformer.model;

public interface ProjectileTarget extends Obstacle {
    void hitBy(Bullet bullet);
}
