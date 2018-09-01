package de.drdelay.aobots.common.contracts;

import de.drdelay.aobots.common.exceptions.EscHitException;

public interface AbortsOnEsc {
    void escapeHit(EscHitException e);
}
