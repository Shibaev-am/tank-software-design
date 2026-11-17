package ru.mipt.bit.platformer.level;

import java.io.IOException;

/** Выходной порт загрузки начального уровня. */
@FunctionalInterface
public interface LevelLoader {
    Level load() throws IOException;
}
