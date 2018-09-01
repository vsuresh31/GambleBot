package de.drdelay.aobots.common.utils;

import de.drdelay.aobots.common.contracts.ShowStatusAble;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StreamLogger implements ShowStatusAble {
    private PrintStream stream;

    public StreamLogger(PrintStream stream) {
        this.stream = stream;
    }

    private static String getDateTime() {
        //noinspection SpellCheckingInspection
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    @Override
    public void addLogLine(String line) {
        stream.println(getDateTime() + ": " + line);
    }

    @Override
    public void setStatus(String status) {
        stream.println(" --- " + status);
    }
}
