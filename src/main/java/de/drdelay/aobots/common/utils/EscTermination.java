package de.drdelay.aobots.common.utils;

import de.drdelay.aobots.common.contracts.AbortsOnEsc;
import de.drdelay.aobots.common.exceptions.EscHitException;
import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;

public class EscTermination {
    private GlobalKeyboardHook keyboardHook;
    private final AbortsOnEsc mainThread;

    public EscTermination(AbortsOnEsc mainThread) {
        this.mainThread = mainThread;
    }

    public void register() {
        if (keyboardHook != null) {
            throw new RuntimeException("Call to EscTermination register that was already registered");
        }

        keyboardHook = new GlobalKeyboardHook(true);
        keyboardHook.addKeyListener(
                new GlobalKeyAdapter() {
                    @Override
                    public void keyPressed(GlobalKeyEvent event) {
                        if (event.getVirtualKeyCode() == GlobalKeyEvent.VK_ESCAPE) {
                            escapeHit();
                        }
                    }
                });
    }

    private void escapeHit() {
        EscHitException e = new EscHitException();
        mainThread.escapeHit(e);
        shutdown();
        throw e;
    }

    public void shutdown() {
        if (keyboardHook != null) {
            keyboardHook.shutdownHook();
            keyboardHook = null;
        }
    }
}
