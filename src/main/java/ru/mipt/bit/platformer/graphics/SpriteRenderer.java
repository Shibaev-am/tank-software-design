package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Disposable;

/** Внутренний интерфейс графического адаптера, зависит от libGDX. */
public interface SpriteRenderer extends Disposable {
    void render(Batch batch);
}
