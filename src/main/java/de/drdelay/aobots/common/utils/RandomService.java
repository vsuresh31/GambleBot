package de.drdelay.aobots.common.utils;

import java.util.concurrent.ThreadLocalRandom;

public class RandomService {
    public static int randInt(int max) {
        if (max == 0) {
            return 0;
        }
        return ThreadLocalRandom.current().nextInt(0, max);
    }

    public static boolean coin() {
        return ThreadLocalRandom.current().nextBoolean();
    }
}
