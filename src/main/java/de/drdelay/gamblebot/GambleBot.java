package de.drdelay.gamblebot;

import de.drdelay.aobots.common.contracts.AbortsOnEsc;
import de.drdelay.aobots.common.contracts.ShowStatusAble;
import de.drdelay.aobots.common.exceptions.EscHitException;
import de.drdelay.aobots.common.exceptions.RuntimeException;
import de.drdelay.aobots.common.utils.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static de.drdelay.aobots.common.utils.DelayService.waitTime;

public class GambleBot implements AbortsOnEsc {
    // Dependencies
    private ShowStatusAble logger;
    private HumanEmulatingRobot robot;

    // Positions determined at start
    private Point prefGm = null;
    private Point prefRe = null;
    private Point suffGm = null;
    private Point suffRe = null;
    private Point item = null;
    private Point itemIn = null;
    private Point factor = null;
    private Point sourceIn = null;
    private BufferedImage enchant;

    // config.txt
    private ArrayList<BufferedImage> prefixImg = null;
    private ArrayList<BufferedImage> suffixImg = null;

    // Iteration counters
    private boolean prefixFound = false;
    private boolean suffixFound = false;
    private int gambles = 0;

    // Config
    private Integer factordelay;
    private Integer showdelay;
    private Integer numberW;
    private Integer inviSize;

    private EscHitException shouldStop;

    // Offsets /WarOfTheSky / EP3:
    /*
    private final static int factorButtonXOffset = 545;
    private final static int factorButtonYOffset = 203;
    private final static int itemFactoryInputXOffset = 467;
    private final static int itemFactoryInputYOffset = 68;
    */

    // Offsets EP4 / CR
    private final static int factorButtonXOffset = 632;
    private final static int factorButtonYOffset = -24;
    private final static int itemFactoryInputXOffset = 548;
    private final static int itemFactoryInputYOffset = -157;

    public void run() throws RuntimeException {
        EscTermination escSrv = new EscTermination(this);
        escSrv.register();
        try {
            doRun();
        } finally {
            escSrv.shutdown();
        }
    }

    private void doRun() {
        logger.setStatus("Initializing");
        init();
        logger.setStatus("Ready");
        loopWeapons();
    }

    private void loopWeapons() {
        logger.setStatus("Running");
        for (int itemi = 0; itemi < numberW; itemi++) {
            checkGambleModeByFixes();
            logger.addLogLine("Starting weapon " + itemi + " of " + (numberW - 1));
            logger.setStatus("Weapon " + itemi + " of " + (numberW - 1));
            gambleWeapon();
            logger.addLogLine("Finish weapon " + itemi + " of " + (numberW - 1) + ", resetting counters");
            prefixFound = false;
            suffixFound = false;
            gambles = 0;
        }
        logger.setStatus("Done");
    }

    private void checkGambleModeByFixes() {
        if (prefixImg.isEmpty()) {
            logger.addLogLine("Not gambling prefixes because empty fixlist");
            prefixFound = true;
        }

        if (suffixImg.isEmpty()) {
            logger.addLogLine("Not gambling suffixes because empty fixlist");
            suffixFound = true;
        }
    }

    private void gambleWeapon() {
        boolean fresh = true;
        while (!prefixFound || !suffixFound) {
            checkExit();
            removeFixes(fresh);
            fresh = false;
            checkExit();
            addFixes();
        }
    }

    public GambleBot(ShowStatusAble logger, int factordelay, int showdelay, int numberW, int inviSize) throws RuntimeException {
        try {
            robot = new HumanEmulatingRobot();
        } catch (AWTException e) {
            throw new RuntimeException("Failed to initialize Robot", e);
        }

        enchant = FS.loadRequiredImage("enchant.png");

        this.logger = logger;

        this.factordelay = factordelay;
        this.showdelay = showdelay;
        this.numberW = numberW;
        this.inviSize = inviSize;

        logger.setStatus("Created");
    }

    private void enchantDone() {
        robot.moveHuman(item, 120);
        //waitTime(3380); // WarOfTheSky
    }

    private void removeFixes(boolean fresh) {
        if (fresh) {
            robot.mouseMove(itemIn.x, itemIn.y);
        } else {
            robot.mouseMove(item.x, item.y);
        }
        waitTime(300);
        checkFixes();

        if (prefixFound && suffixFound) {
            logger.addLogLine("Pre- and Suffix found after " + gambles + " gambles");
            return;
        }

        moveItemIntoLab();
        waitTime(75);
        if (!prefixFound && !suffixFound) {
            if (RandomService.coin()) {
                preRe();
                waitTime(75);
                sufRe();
            } else {
                sufRe();
                waitTime(75);
                preRe();
            }
        } else if (prefixFound && !suffixFound) {
            sufRe();
        } else if (!prefixFound) {
            preRe();
        }
        waitTime(75);
        factor();
        enchantDone();
    }

    private void addFixes() {
        if (prefixFound && suffixFound) {
            return;
        }

        itemIn(item);
        waitTime(75);
        gambles++;
        if (!prefixFound && !suffixFound) {
            if (RandomService.coin()) {
                preGa();
                waitTime(75);
                sufGa();
            } else {
                sufGa();
                waitTime(75);
                preGa();
            }
        } else if (prefixFound && !suffixFound) {
            sufGa();
        } else if (!prefixFound) {
            preGa();
        }
        waitTime(75);
        factor();
        enchantDone();
        waitTime(75);
    }

    private void itemIn(Point item) {
        robot.mouseMove(item.x, item.y);
        waitTime(75);
        moveItemIntoLab();
    }

    private void preRe() {
        itemIn(prefRe);
    }

    private void sufRe() {
        itemIn(suffRe);
    }

    private void preGa() {
        itemIn(prefGm);
    }

    private void sufGa() {
        itemIn(suffGm);
    }

    private void moveItemIntoLab() {
        checkExit();
        //noinspection ConstantConditions
        if (false) { // todo: switch for double click supported (EP4?)
            robot.doubleClick();
        } else {
            robot.dragDrop(sourceIn);
        }
    }

    private void init() {
        BufferedImage screen = robot.screenShot();

        Point searcheye = ScreenCoordinateTools.findRequiredItem("searcheye.png", screen);

        item = ScreenCoordinateTools.calculateItemAtIndex(this.inviSize, searcheye);
        factor = new Point(searcheye.x + factorButtonXOffset, searcheye.y + factorButtonYOffset);
        sourceIn = new Point(searcheye.x + itemFactoryInputXOffset, searcheye.y + itemFactoryInputYOffset);

        loadCfg();
        checkGambleModeByFixes();

        if (!prefixFound) {
            prefGm = ScreenCoordinateTools.findRequiredItem("prefixGmbl.png", screen);
            prefRe = ScreenCoordinateTools.findRequiredItem("prefixReset.png", screen);
            logger.addLogLine("Prefix Gamble found at " + prefGm.x + "/" + prefGm.y);
            logger.addLogLine("Prefix Removal found at " + prefRe.x + "/" + prefRe.y);
        }
        if (!suffixFound) {
            suffGm = ScreenCoordinateTools.findRequiredItem("suffixGmbl.png", screen);
            suffRe = ScreenCoordinateTools.findRequiredItem("suffixReset.png", screen);
            logger.addLogLine("Suffix Gamble found at " + suffGm.x + "/" + suffGm.y);
            logger.addLogLine("Suffix Removal found at " + suffRe.x + "/" + suffRe.y);
        }

        itemIn = ScreenCoordinateTools.calculateItemAtIndex((this.inviSize - numberW) + 1, searcheye);

        logger.addLogLine("Item assumed (relative to searcheye-pos) at " + item.getX() + "/" + item.getY());
        logger.addLogLine("ItemIn set to (relative to searcheye-pos) " + itemIn.getX() + "/" + itemIn.getY());
        logger.addLogLine("FactorButton assumed (relative to searcheye-pos) at " + factor.getX() + "/" + factor.getY());
        logger.addLogLine("SourceIn assumed (relative to searcheye-pos) at " + sourceIn.getX() + "/" + sourceIn.getY());
    }

    private void checkFixes() {
        BufferedImage itemname = findItemName();
        int middle = itemname.getWidth() / 2;
        int middleoffset = middle / 10;
        if (!prefixFound) {
            BufferedImage leftname = itemname.getSubimage(0, 0, middle + middleoffset, itemname.getHeight());
            for (BufferedImage fix : prefixImg) {
                if (ImageTools.findSubImgMatchingPoint(fix, leftname) != null) {
                    logger.addLogLine("Prefix found after " + gambles + " gambles: FixPos in cfg: " + (prefixImg.indexOf(fix) + 1));
                    prefixFound = true;
                    break;
                }
            }
        }
        if (!suffixFound) {
            BufferedImage rightname = itemname.getSubimage(middle - middleoffset, 0, middle + middleoffset, itemname.getHeight());
            for (BufferedImage fix : suffixImg) {
                if (ImageTools.findSubImgMatchingPoint(fix, rightname) != null) {
                    logger.addLogLine("Suffix found after " + gambles + " gambles: FixPos in cfg: " + (suffixImg.indexOf(fix) + 1));
                    suffixFound = true;
                    break;
                }
            }
        }
        waitTime(showdelay);
    }

    private void loadCfg() {
        ArrayList<String> prefixes = new ArrayList<>();
        ArrayList<String> suffixes = new ArrayList<>();
        ArrayList<String> fill = prefixes;
        try {
            try (BufferedReader br = new BufferedReader(new FileReader("config.txt"))) {
                for (String line; (line = br.readLine()) != null; ) {
                    line = line.trim();
                    if (line.length() == 0) {
                        continue;
                    }
                    if (line.equals("-")) {
                        fill = suffixes;
                        continue;
                    }
                    fill.add(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Config file read error", e);
        }
        logger.addLogLine(prefixes.size() + " Prefixes loaded from cfg: " + prefixes);
        logger.addLogLine(suffixes.size() + " Suffixes loaded from cfg: " + suffixes);
        prefixImg = new ArrayList<>();
        suffixImg = new ArrayList<>();
        prefixImg.addAll(prefixes.stream().map(FS::loadRequiredImage).collect(Collectors.toList()));
        suffixImg.addAll(suffixes.stream().map(FS::loadRequiredImage).collect(Collectors.toList()));
    }

    private BufferedImage findItemName() {
        BufferedImage screen = findItemNameOnScreen();
        if (screen == null) {
            logger.addLogLine("Factoring one more time in 1s and trying again before Termination");
            waitTime(1000);
            factor();
            waitTime(50);
            robot.mouseMove(item.x, item.y);
            waitTime(300);
            screen = findItemNameOnScreen();
            if (screen == null) {
                throw new RuntimeException("Failed to find Item-Name by enchant.png search");
            }
        }
        return screen;
    }

    private BufferedImage findItemNameOnScreen() {
        BufferedImage screen = robot.screenShot();
        int xoff = itemIn.x - 15;
        int xwp = item.x - itemIn.x;
        screen = screen.getSubimage(xoff, 0, 475 + xwp, screen.getHeight());
        Point enPos = ImageTools.findSubImgMatchingPoint(enchant, screen);
        if (enPos == null) {
            return null;
        }
        return screen.getSubimage(Math.max(enPos.x - 154, 0), enPos.y + 18, 334, 22);
    }

    private void factor() {
        checkExit();
        robot.mouseMove(factor.x, factor.y);
        robot.click();
        waitTime(factordelay);
        robot.click();
    }

    @Override
    public void escapeHit(EscHitException e) {
        this.shouldStop = e;
        this.logger.addLogLine("Caught Esc");
        this.logger.setStatus("Exiting");
    }

    // Move to some sort of trait?
    private void checkExit() {
        if (this.shouldStop != null) {
            throw this.shouldStop;
        }
    }
}
