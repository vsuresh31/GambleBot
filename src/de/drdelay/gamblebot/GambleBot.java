package de.drdelay.gamblebot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

public class GambleBot {
    private static Robot robot = null;
    private static Point prefGm = null;
    private static Point prefRe = null;
    private static Point suffGm = null;
    private static Point suffRe = null;
    private static Point item = null;
    private static Point itemIn = null;
    private static Point factor = null;
    private static BufferedImage enchant = null;
    private static BufferedImage bloodfix = null;
    private static ArrayList<BufferedImage> prefixImg = null;
    private static ArrayList<BufferedImage> suffixImg = null;
    private static boolean prefixFound = false;
    private static boolean suffixFound = false;
    private static int gambles = 0;
    private static Integer factordelay;
    private static Integer showdelay;
    private static Integer numberW;

    public static void main(String args[]) {
        System.out.println("Run this programm as admin or the Robot will not work");
        System.out.println("You may specify the factordelay (1) and showdelay (2) and numberOfWeaps (3) via cmdline-params in ms");
        System.out.println("Put a searcheye-stack at the top left corner of your inventory");
        System.out.println("Put your gamble-item in Pos 56");
        System.out.println("Item must not have only 1 fix and must at least be Enchant 1 at the beginning");
        System.out.println();
        try {
            robot = new Robot();
        } catch (AWTException e) {
            System.out.println(getDateTime() + ": Failed to create Robot: " + e.getMessage());
            System.exit(1);
        }
        System.out.println("Robot#" + robot.hashCode() + " created, focus the gamewindow");
        System.out.println();
        waitTime(4000);
        String arg1 = null;
        String arg2 = null;
        String arg3 = null;
        if (args.length > 0) {
            arg1 = args[0];
            if (args.length > 1) {
                arg2 = args[1];
                if (args.length > 2) {
                    arg3 = args[2];
                }
            }
        }
        init(arg1, arg2, arg3);
        //noinspection UnusedAssignment
        args = null;
        //noinspection UnusedAssignment
        arg1 = null;
        //noinspection UnusedAssignment
        arg2 = null;
        //noinspection UnusedAssignment
        arg3 = null;
        System.out.println();
        System.out.println("Prefix Gamble found at " + prefGm.x + "/" + prefGm.y);
        System.out.println("Prefix Removal found at " + prefRe.x + "/" + prefRe.y);
        System.out.println("Suffix Gamble found at " + suffGm.x + "/" + suffGm.y);
        System.out.println("Suffix Removal found at " + suffRe.x + "/" + suffRe.y);
        System.out.println("Item assumed (relative to searcheye-pos) at " + item.getX() + "/" + item.getY());
        System.out.println("ItemIn set to (relative to searcheye-pos) " + itemIn.getX() + "/" + itemIn.getY());
        System.out.println("FactorButton assumed (relative to searcheye-pos) at " + factor.getX() + "/" + factor.getY());
        System.out.println();
        for (int itemi = 0; itemi < numberW; itemi++) {
            boolean fresh = true;
            System.out.println(getDateTime() + ": Starting weapon " + itemi + " of " + (numberW - 1));
            while (!prefixFound || !suffixFound) {
                removeFixes(fresh);
                fresh = false;
                addFixes();
            }
            System.out.println(getDateTime() + ": Finish weapon " + itemi + " of " + (numberW - 1) + ", resetting counters");
            System.out.println();
            prefixFound = false;
            suffixFound = false;
            gambles = 0;
        }
        System.out.print(getDateTime() + ": Finish all!");
    }

    private static String getDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    private static void removeFixes(boolean fresh) {
        if (fresh) {
            robot.mouseMove(itemIn.x, itemIn.y);
        } else {
            robot.mouseMove(item.x, item.y);
        }
        waitTime(100);
        checkFixes();
        doubleClick();
        waitTime(75);
        if (!prefixFound && !suffixFound) {
            preRe();
            sufRe();
        } else if (prefixFound && !suffixFound) {
            sufRe();
        } else if (!prefixFound) {
            preRe();
        } else {
            System.out.println(getDateTime() + ": Pre- and Suffix found after " + gambles + " gambles");
        }
        waitTime(75);
        factor();
    }

    private static void addFixes() {
        robot.mouseMove(item.x, item.y);
        waitTime(45);
        doubleClick();
        waitTime(75);
        gambles++;
        if (!prefixFound && !suffixFound) {
            preGa();
            sufGa();
        } else if (prefixFound && !suffixFound) {
            sufGa();
        } else if (!prefixFound) {
            preGa();
        }
        waitTime(75);
        factor();
    }

    private static void preRe() {
        robot.mouseMove(prefRe.x, prefRe.y);
        waitTime(75);
        doubleClick();
    }

    private static void sufRe() {
        robot.mouseMove(suffRe.x, suffRe.y);
        waitTime(75);
        doubleClick();
    }

    private static void preGa() {
        robot.mouseMove(prefGm.x, prefGm.y);
        waitTime(75);
        doubleClick();
    }

    private static void sufGa() {
        robot.mouseMove(suffGm.x, suffGm.y);
        waitTime(75);
        doubleClick();
    }

    private static void click() {
        robot.mousePress(InputEvent.BUTTON1_MASK);
        waitTime(45);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    private static void doubleClick() {
        click();
        waitTime(50);
        click();
    }

    private static BufferedImage screenShot() {
        return robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
    }

    private static void init(String factord, String showd, String numW) {
        BufferedImage screen = screenShot();
        Point searcheye = subImgFromFile("searcheye.png", screen);
        assert searcheye != null;
        item = new Point(searcheye.x + 157, searcheye.y + 159); // 223 - 2*33 = 124 @ 17.07.2016 for DA
        factor = new Point(searcheye.x + 631, searcheye.y - 29);
        prefGm = subImgFromFile("prefixGmbl.png", screen);
        prefRe = subImgFromFile("prefixReset.png", screen);
        suffGm = subImgFromFile("suffixGmbl.png", screen);
        suffRe = subImgFromFile("suffixReset.png", screen);
        enchant = readFile("enchant.png");
        bloodfix = readFile("Blood.png");
        loadCfg();
        System.out.println();
        factordelay = parsePosOrDef(factord, 1500);
        showdelay = parsePosOrDef(showd, 500);
        numberW = parsePosOrDef(numW, 1);
        int ipos = 57 - numberW; // 59 - 2 = 57 @ 17.07.2016 for DA
        itemIn = new Point(searcheye.x + (((ipos - 1)) % 10) * 33, searcheye.y + (((ipos - 1)) / 10) * 33);
        System.out.println("factordelay set to " + factordelay);
        System.out.println("showdelay set to " + showdelay);
        System.out.println("numberW set to " + numberW);
    }

    private static int parsePosOrDef(String param, int def) {
        if (param != null) {
            int parsefactor;
            try {
                parsefactor = Integer.parseInt(param);
                if (parsefactor > 0) {
                    return parsefactor;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return def;
    }

    private static void checkFixes() {
        BufferedImage itemname = findItemName();
        int middle = itemname.getWidth() / 2;
        int middleoffset = middle / 10;
        if (!prefixFound) {
            BufferedImage leftname = itemname.getSubimage(0, 0, middle + middleoffset, itemname.getHeight());
            for (BufferedImage fix : prefixImg) {
                if (subImg(fix, leftname) != null) {
                    if (subImg(bloodfix, leftname) != null) {
                        System.out.println("Found Blood-Prefix, skipping");
                        continue;
                    }
                    System.out.println(getDateTime() + ": Prefix found after " + gambles + " gambles: FixPos in cfg: " + (prefixImg.indexOf(fix) + 1));
                    prefixFound = true;
                    break;
                }
            }
        }
        if (!suffixFound) {
            BufferedImage rightname = itemname.getSubimage(middle - middleoffset, 0, middle + middleoffset, itemname.getHeight());
            for (BufferedImage fix : suffixImg) {
                if (subImg(fix, rightname) != null) {
                    if (subImg(bloodfix, rightname) != null) {
                        System.out.println("Found Blood-Suffix, skipping");
                        continue;
                    }
                    System.out.println(getDateTime() + ": Suffix found after " + gambles + " gambles: FixPos in cfg: " + (suffixImg.indexOf(fix) + 1));
                    suffixFound = true;
                    break;
                }
            }
        }
        waitTime(showdelay);
    }

    private static void loadCfg() {
        ArrayList<String> prefixes = new ArrayList<>();
        ArrayList<String> suffixes = new ArrayList<>();
        ArrayList<String> fill = prefixes;
        try {
            try (BufferedReader br = new BufferedReader(new FileReader("config.txt"))) {
                for (String line; (line = br.readLine()) != null; ) {
                    if (line.equals("-")) {
                        fill = suffixes;
                        continue;
                    }
                    fill.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println(getDateTime() + ": Config file read error!");
            System.exit(8);
        }
        System.out.println(prefixes.size() + " Prefixes loaded from cfg: " + prefixes);
        System.out.println(suffixes.size() + " Suffixes loaded from cfg: " + suffixes);
        prefixImg = new ArrayList<>();
        suffixImg = new ArrayList<>();
        prefixImg.addAll(prefixes.stream().map(GambleBot::readFile).collect(Collectors.toList()));
        suffixImg.addAll(suffixes.stream().map(GambleBot::readFile).collect(Collectors.toList()));
    }

    private static BufferedImage findItemName() {
        BufferedImage screen = screenShot();
        int xoff = itemIn.x - 15;
        int xwp = item.x - itemIn.x;
        screen = screen.getSubimage(xoff, 0, 475 + xwp, screen.getHeight());
        Point enPos = subImg(enchant, screen);
        if (!validatePoint(enPos, "enchant.png", true)) {
            System.out.println(getDateTime() + ": Factoring one more time in 1s and trying again before Termination");
            waitTime(1000);
            factor();
            waitTime(50);
            robot.mouseMove(item.x, item.y);
            waitTime(75);
            screen = screenShot();
            screen = screen.getSubimage(xoff, 0, 475 + xwp, screen.getHeight());
            enPos = subImg(enchant, screen);
            validatePoint(enPos, "enchant.png", false);
        }
        assert enPos != null;
        return screen.getSubimage(enPos.x - 154, enPos.y + 18, 334, 17);
    }

    private static boolean validatePoint(Point p, String fromFile, boolean soft) {
        if (p == null) {
            System.out.println(getDateTime() + ": Failed to find image '" + fromFile + "' on screen");
            if (soft) {
                return false;
            } else {
                System.out.println(getDateTime() + ": Rest in Paprikas");
                System.exit(3);
            }
        }
        return true;
    }

    private static void factor() {
        robot.mouseMove(factor.x, factor.y);
        click();
        waitTime(factordelay);
        click();
    }

    private static BufferedImage readFile(String name) {
        try {
            return ImageIO.read(new File(name));
        } catch (IOException e) {
            System.out.println(getDateTime() + ": Failed to open file " + name + ": " + e.getMessage());
            System.exit(2);
        }
        return null;
    }

    private static Point subImgFromFile(String needleFile, BufferedImage haystack) {
        Point ret = subImg(readFile(needleFile), haystack);
        validatePoint(ret, needleFile, false);
        return ret;
    }

    private static Point subImg(BufferedImage needle, BufferedImage haystack) {
        int hayW = haystack.getWidth() - needle.getWidth();
        int hayH = haystack.getHeight() - needle.getHeight();
        for (int hIx = 0; hIx < hayW; hIx++) {
            for (int hIy = 0; hIy < hayH; hIy++) {
                if (compareImg(needle, haystack, hIx, hIy)) {
                    int toMidX = 13;
                    int toMidY = 3;
                    return new Point(hIx + toMidX, hIy + toMidY);
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

    private static boolean isTransp(int pixel) {
        return pixel == -16777216;
    }

    private static void waitTime(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            System.out.println("InterruptedException while waiting: " + e.getMessage());
        }
    }
}
