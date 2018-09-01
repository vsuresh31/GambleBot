package de.drdelay.aobots.common.utils;

import de.drdelay.aobots.common.exceptions.RuntimeException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FS {
    private static File getResFileByName(String name) {
        // Prefix with res?
        return new File(name);
    }

    public static BufferedImage readImageFile(String name) throws IOException {
        return ImageIO.read(getResFileByName(name));
    }

    public static BufferedImage loadRequiredImage(String name) throws RuntimeException {
        try {
            return readImageFile(name);
        } catch (IOException e) {
            throw new RuntimeException(name + " could not be loaded", e);
        }
    }
}
