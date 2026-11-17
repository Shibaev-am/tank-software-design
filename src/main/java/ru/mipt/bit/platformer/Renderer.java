package ru.mipt.bit.platformer;

/** Выходной порт application: отрисовка кадра и освобождение ресурсов. */
public interface Renderer {
    void render();
    void dispose();
}
