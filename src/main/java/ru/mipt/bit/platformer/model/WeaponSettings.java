package ru.mipt.bit.platformer.model;

public final class WeaponSettings {
    private final int damage;
    private final float bulletTileDuration;
    private final float reloadDuration;

    public WeaponSettings(int damage, float bulletTileDuration, float reloadDuration) {
        if (damage <= 0 || !Float.isFinite(bulletTileDuration) || bulletTileDuration <= 0f
                || !Float.isFinite(reloadDuration) || reloadDuration <= 0f) {
            throw new IllegalArgumentException("Weapon settings must be positive and finite");
        }
        this.damage = damage;
        this.bulletTileDuration = bulletTileDuration;
        this.reloadDuration = reloadDuration;
    }

    public int getDamage() {
        return damage;
    }

    public float getBulletTileDuration() {
        return bulletTileDuration;
    }

    public float getReloadDuration() {
        return reloadDuration;
    }
}
