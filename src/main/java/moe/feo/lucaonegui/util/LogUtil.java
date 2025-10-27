package moe.feo.lucaonegui.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {
    private static final String LOG_FILE = "log.txt";

    public static void log(String message) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String fullMessage = "[" + timestamp + "] " + message;

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(LOG_FILE, true), StandardCharsets.UTF_8))) {
            writer.write(fullMessage);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Failed to write " + LOG_FILE + ": " + e.getMessage());
        }
    }
}
