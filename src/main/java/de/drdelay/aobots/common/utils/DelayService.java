package de.drdelay.aobots.common.utils;

public class DelayService {
    public static void waitExactly(int millis) {
        doWait(millis);
    }

    private static void doWait(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            System.out.println("InterruptedException while waiting: " + e.getMessage());
        }
    }

    private static int randomizeWaitTime(int millis) {
        int maxRandAdditional = millis > 20 ? millis / 10 : 0;
        return millis + RandomService.randInt(maxRandAdditional);
    }

    public static void waitTime(int millis) {
        int delay = randomizeWaitTime(millis);
        doWait(delay);
    }
}
