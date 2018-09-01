package de.drdelay.aobots.common.utils;

import de.drdelay.aobots.common.exceptions.RuntimeException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ScreenCoordinateTools {
    private static Point moveToItemCenter(Point topleftOfItem) {
        Point move = (Point) topleftOfItem.clone();
        move.translate(13, 3);
        return move;
    }

    public static Point findItem(BufferedImage needle, BufferedImage haystack) {
        Point search = ImageTools.findSubImgMatchingPoint(needle, haystack);
        if (search == null) {
            return null;
        }
        return moveToItemCenter(search);
    }

    public static Point findItem(String needleFile, BufferedImage haystack) throws IOException {
        return findItem(FS.readImageFile(needleFile), haystack);
    }

    public static Point findRequiredItem(String needleFile, BufferedImage haystack) throws RuntimeException {
        Point found = findItem(FS.loadRequiredImage(needleFile), haystack);
        if (found == null) {
            throw new RuntimeException(needleFile + " not found on screen");
        }
        return found;
    }

    public static Point calculateItemAtIndex(int index, Point itemAtPos1) {

        // refPoint: 10/10 - index 1 -> expect 10/10
        // refPoint: 10/10 - index 2 -> expect 43/10
        // refPoint: 10/10 - index 10 -> expect 307/10
        // refPoint: 10/10 - index 11 -> expect 10/33
        // refPoint: 10/10 - index 13 -> expect 76/33

        return new Point(itemAtPos1.x + (((index - 1)) % 10) * 33, itemAtPos1.y + (((index - 1)) / 10) * 33);
    }
}
