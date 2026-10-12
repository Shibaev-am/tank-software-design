package ru.mipt.bit.platformer.level;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public final class LevelOptions {
    private LevelOptions() {
    }

    public static LevelLoader fromArguments(String[] args) {
        if (args.length == 0) {
            return new FileLevelLoader(() -> LevelOptions.class.getResourceAsStream("/levels/default.txt"));
        }
        if ("--level".equals(args[0]) && args.length == 2) {
            Path file = Path.of(args[1]);
            return new FileLevelLoader(() -> Files.newInputStream(file));
        }
        if ("--random".equals(args[0]) && (args.length == 1 || args.length == 2)) {
            Random random = args.length == 2 ? new Random(Long.parseLong(args[1])) : new Random();
            return new RandomLevelLoader(10, 8, 16, random);
        }
        throw new IllegalArgumentException("Usage: [--level <file> | --random [seed]]");
    }
}
