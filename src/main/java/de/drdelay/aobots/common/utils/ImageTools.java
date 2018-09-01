package de.drdelay.aobots.common.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageTools {
    private static boolean isTransp(int pixel) {
        return pixel == -16777216;
    }

    public static Point findSubImgMatchingPoint(BufferedImage needle, BufferedImage haystack) {
        int hayW = haystack.getWidth() - needle.getWidth();
        int hayH = haystack.getHeight() - needle.getHeight();
        for (int hIx = 0; hIx < hayW; hIx++) {
            for (int hIy = 0; hIy < hayH; hIy++) {
                if (compareImg(needle, haystack, hIx, hIy)) {
                    return new Point(hIx, hIy);
                }
            }
        }
        return null;
    }

    private static boolean compareImg(BufferedImage needle, BufferedImage haystack, int hayOffX, int hayOffY) {
        int needW = needle.getWidth() - 1;
        int needH = needle.getHeight() - 1;
        for (int nIx = 0; nIx < needW; nIx++) {
            for (int nIy = 0; nIy < needH; nIy++) {
                int needleRGB = needle.getRGB(nIx, nIy);
                int haystackRGB = haystack.getRGB(hayOffX + nIx, hayOffY + nIy);
                if (needleRGB != haystackRGB && !isTransp(needleRGB)) {
                    return false;
                }
            }
        }
        return true;
    }
}
