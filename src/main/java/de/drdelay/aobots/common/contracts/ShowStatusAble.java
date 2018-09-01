package de.drdelay.aobots.common.contracts;

public interface ShowStatusAble {
    void addLogLine(String line);

    void setStatus(String status);
}
