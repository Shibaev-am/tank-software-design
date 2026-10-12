package ru.mipt.bit.platformer.level;

import java.io.IOException;

@FunctionalInterface
public interface LevelLoader {
    Level load() throws IOException;
}
