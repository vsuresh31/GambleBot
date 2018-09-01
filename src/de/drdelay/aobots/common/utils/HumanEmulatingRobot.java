package de.drdelay.aobots.common.utils;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;

import static de.drdelay.aobots.common.utils.DelayService.waitTime;

public class HumanEmulatingRobot extends Robot {

    public HumanEmulatingRobot() throws AWTException {
        super();
    }

    public void moveHuman(Point to, int time) {
        int moveTimeSteps = 10;

        Point start = MouseInfo.getPointerInfo().getLocation();

        int steps = time / moveTimeSteps;

        int startX = start.x;
        int startY = start.y;

        int moveXPerStep = (to.x - startX) / steps;
        int moveYPerStep = (to.y - startY) / steps;

        for (int i = 0; i < steps; i++) {
            startX += moveXPerStep;
            startY += moveYPerStep;
            this.mouseMove(startX, startY);
            waitTime(moveTimeSteps);
        }

        // always "beam" to the final destination to assure the mouse ends there, even the loop may not have done it.
        this.mouseMove(to.x, to.y);
    }

    public void click() {
        this.mousePress(InputEvent.BUTTON1_MASK);
        waitTime(45);
        this.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    public void doubleClick() {
        click();
        waitTime(50);
        click();
    }

    public BufferedImage screenShot() {
        return this.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
    }

    public void dragDrop(Point to) {
        // drag
        this.mousePress(InputEvent.BUTTON1_MASK);

        waitTime(103);

        // drop
        this.moveHuman(to, 43);
        this.mouseRelease(InputEvent.BUTTON1_MASK);
    }
}
