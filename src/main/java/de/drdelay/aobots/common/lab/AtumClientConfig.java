package de.drdelay.aobots.common.lab;

import java.awt.Point;

public class AtumClientConfig {
    public static Point getFactorButtonOffset(String client) {
        switch (client) {
            case "ep3":
                return new Point(545, 203);
            case "ep4":
                return new Point(632, -24);
        }
        return null;
    }

    public static Point getItemFactoryInputOffset(String client) {
        switch (client) {
            case "ep3":
                return new Point(467, 68);
            case "ep4":
                return new Point(548, -157);
        }
        return null;
    }
}
