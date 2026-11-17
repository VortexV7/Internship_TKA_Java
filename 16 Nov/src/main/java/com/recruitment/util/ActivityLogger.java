package com.recruitment.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ActivityLogger - simple file handling (Spec section 13).
 * Appends one line per activity to activity_log.txt in the project folder,
 * e.g. "Candidate Created: CAN1001".
 */
public class ActivityLogger {

    private static final String LOG_FILE = "activity_log.txt";
    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Appends "action: record" with a timestamp. Never throws - logging must not crash the app. */
    public static synchronized void log(String action, String record) {
        String line = LocalDateTime.now().format(FORMAT)
                + " | " + action + ": " + record;
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            out.println(line);
        } catch (IOException e) {
            System.err.println("Warning: could not write to " + LOG_FILE + " - " + e.getMessage());
        }
    }

    public static void log(String action) {
        log(action, "");
    }
}
