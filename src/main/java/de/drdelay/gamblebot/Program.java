package de.drdelay.gamblebot;

import de.drdelay.aobots.common.utils.DelayService;
import de.drdelay.aobots.common.utils.StreamLogger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Program {
    public static void main(String args[]) {
        System.out.println("Welcome to GambleBot!");
        System.out.println();

        String itemPosOverride = null;
        if (args.length >= 1) {
            itemPosOverride = args[0];
        }

        run(itemPosOverride);

    }

    private static void run(String itemPosOverride) {

        Properties defaultProperties = new Properties();
        defaultProperties.setProperty("factordelay", "400");
        defaultProperties.setProperty("showdelay", "1250");
        defaultProperties.setProperty("numberW", "1");
        defaultProperties.setProperty("lastItemPos", "56");

        Properties prop = new Properties(defaultProperties);

        try (InputStream input = new FileInputStream("config.properties")) {
            // load a properties file
            prop.load(input);
        } catch (FileNotFoundException e) {
            System.out.println("No config.properties found. Using defaults");
        } catch (IOException e) {
            System.err.println("Failed to load config");
            e.printStackTrace();
        }

        GambleBot instance = new GambleBot(
                new StreamLogger(System.out),
                Integer.parseInt(prop.getProperty("factordelay")),
                Integer.parseInt(prop.getProperty("showdelay")),
                Integer.parseInt(prop.getProperty("numberW")),
                Integer.parseInt((itemPosOverride != null) ? itemPosOverride : prop.getProperty("lastItemPos"))
        );

        System.out.println("Starting in 4s - focus the game-window!");
        DelayService.waitExactly(4000);
        System.out.println();

        instance.run();
    }
}
