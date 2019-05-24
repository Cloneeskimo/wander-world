package engine;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//Error Codes Used: 0

public class Utils {

    //Static Error Level Ints
    public static final int INFO = 0;
    public static final int WARNING = 1;
    public static final int FATAL = 2;

    //Error Throwing Method
    // @assert - all levels other than the above specified ones will be treated as FATAL
    public static void error(String message, String sourceFile, int code, int level, boolean log) {

        //compile log line
        String levelLine = level == Utils.INFO ? "INFO" : level == Utils.WARNING ? "WARNING" : "FATAL";
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String logLine = "[" + timeFormatter.format(LocalDateTime.now()) + "][" + levelLine + "][" + sourceFile + "]: " + message + " (code: " + code + ")";

        //create exception
        IllegalStateException e = new IllegalStateException(logLine);

        //log error to file
        if (log) Utils.logError(e, sourceFile, code, level);

        //throw exception if fatal, just print it if not
        if (level == Utils.INFO || level == Utils.WARNING) e.printStackTrace();
        else throw e;
    }

    public static void error(String message, String sourceFile, int code, int level) {
        Utils.error(message, sourceFile, code, level, true);
    }

    //Error Logging Method
    private static void logError(Exception e, String sourceFile, int code, int level) {

        //format time and date
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd-yy");
        String fileName = "ErrorLog " + dateFormatter.format(LocalDateTime.now()) + ".txt";

        //try to open log file to print
        try {
            PrintWriter out = new PrintWriter(new FileOutputStream(new File(fileName), true));

            //print
            e.printStackTrace(out);
            out.close();

        } catch (Exception ex) {
            Utils.error("Unable to open file for error logging: " + ex.getMessage(), "engine.Utils", 0, Utils.FATAL, false);
        }
    }

}
