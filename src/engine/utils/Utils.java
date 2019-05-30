package engine.utils;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//Error Codes Used: 0 - 7

public class Utils {

    //Static Error Level Ints
    public static final int INFO = 0; //message will be displayed
    public static final int WARNING = 1; //message and an exception stack trace will be displayed
    public static final int FATAL = 2; //exception will be throw

    //Static ErrorLog Storage Path
    public static final String ERRORLOG_DIRECTORY = "data/errors/";

    /**
     * Will throw a customized error given the provided parameters
     * @param message the error message
     * @param sourceFile the source file from which the error originates
     * @param code the error-specific code
     * @param level the error level (specified at the top of this file). any invalid level provided
     *              will be treated as FATAL
     * @param log whether or not to the log the error
     */
    public static void error(String message, String sourceFile, int code, int level, boolean log) {

        //compile log line
        String levelLine = level == Utils.INFO ? "INFO" : level == Utils.WARNING ? "WARNING" : "FATAL";
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String logLine = "[" + timeFormatter.format(LocalDateTime.now()) + "][" + levelLine + "][" + sourceFile + "]: "
                + message + " (code: " + code + ")";

        //create exception
        IllegalStateException e = new IllegalStateException(logLine);

        //log error to file
        if (log) Utils.logError(e);

        //throw exception if fatal, just print it if not
        if (level == Utils.WARNING) e.printStackTrace();
        else if (level == Utils.INFO) System.out.println(logLine);
        else if (level != Utils.INFO) throw e;
    }

    /**
     * Throws an error by calling the overloaded version of the method above with the same parameters except
     * assuming that the user wants to log the error
     */
    public static void error(String message, String sourceFile, int code, int level) {
        Utils.error(message, sourceFile, code, level, true);
    }

    /**
     * Ensures that a directory exists
     * @param directory the directory to ensure. if exists, will throw an info error
     */
    public static void ensureDirectory(String directory) {
        File dir = new File(directory);
        boolean outcome = dir.mkdirs();
        if (!outcome) Utils.error("Unable to create directories at '" + directory + "', assuming they exist",
                "engine.utils.Utils", 1, Utils.INFO, false);
    }

    /**
     * Loads a resource into a single String
     * @param resourcePath the resource-relative path of the file
     * @return the loaded resource
     */
    public static String loadResourceIntoString(String resourcePath) {
        String result = new String();
        try (InputStream in = Class.forName(Utils.class.getName()).getResourceAsStream(resourcePath);
            Scanner scanner = new Scanner(in, "UTF-8")) {
            result = scanner.useDelimiter("\\A").next();
        } catch (Exception e) {
            Utils.error("Unable to load resource: " + e.getMessage(), "engine.utils.Utils", 3,
                    Utils.FATAL);
        }
        return result;
    }

    /**
     * Loads a resource to a list of strings
     * @return the loaded resource
     */
    public static List<String> loadResourceIntoStringList(String resourcePath) {
        List<String> file = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(Class.forName(Utils.class.getName())
        .getResourceAsStream(resourcePath)))) {
            String line;
            while ((line = in.readLine()) != null) file.add(line);
        } catch (Exception e) {
            Utils.error("Unable to load resource at path: " + resourcePath + ": " + e.getMessage(),
                    "engine.utils.Utils", 5, Utils.FATAL);
        }
        return file;
    }

    /**
     * Logs an error - meant to be called only from the error method
     * @param e the exception for stack trace printing
     */
    private static void logError(Exception e) {

        //format time and date
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd-yy");
        String fileName = "ErrorLog " + dateFormatter.format(LocalDateTime.now()) + ".txt";

        //try to open log file to print
        try {
            Utils.ensureDirectory(ERRORLOG_DIRECTORY);
            PrintWriter out = new PrintWriter(new FileOutputStream(new File(ERRORLOG_DIRECTORY + fileName), true));

            //print
            e.printStackTrace(out);
            out.close();

        } catch (Exception ex) {
            Utils.error("Unable to open file for error logging: " + ex.getMessage(),
                    "engine.utils.Utils", 0, Utils.FATAL, false);
        }
    }
}
