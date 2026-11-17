package ru.mipt.bit.platformer.model;

/** Контракт чтения здоровья domain для отображения. */
public interface HealthState {
    int getHealth();
    int getMaxHealth();

    default float getHealthFraction() {
        return Math.max(0f, Math.min(1f, (float) getHealth() / getMaxHealth()));
    }
}
