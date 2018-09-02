package de.drdelay.aobots.common.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Debug {
    public static void debugSave(BufferedImage img) {
        try {
            File outputfile = new File(System.currentTimeMillis() + ".png");
            ImageIO.write(img, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
