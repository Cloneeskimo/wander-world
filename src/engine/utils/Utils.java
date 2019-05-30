package engine.utils;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//Log Codes Used: 0

public class Utils {

    //Static ErrorLog Storage Path
    public static final String LOG_DIRECTORY = "data/logs/";

    /**
     * Ensures that a directory exists
     * @param directory the directory to ensure. if exists, will throw an info error
     */
    public static void ensureDirectory(String directory) {
        File dir = new File(directory);
        boolean outcome = dir.mkdirs();
        if (!outcome) Utils.log("Unable to create directories at '" + directory + "', assuming they exist",
                "engine.utils.Utils", 0, false);
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
            Utils.log(e, "engine.utils.Utils");
            e.printStackTrace();
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
            Utils.log(e, "engine.utils.Utils");
            e.printStackTrace();
        }
        return file;
    }

    /**
     * Logs an exception
     * @param e the exception to log
     * @param sourceFile the source code file from which the exception originates
     * @note all exceptions are logged with the code 0, since their stack traces point to where they originate
     *       anyways
     */
    public static void log(Exception e, String sourceFile) {

        //get file and log lines
        String fileName = getFileLine();
        String logLine = getLogLine(true, sourceFile, 0);

        //write to file file
        try {
            PrintWriter out = new PrintWriter(new FileOutputStream(new File(fileName), true));

            //print stack trace to file, close file
            out.print(logLine);
            e.printStackTrace(out);
            out.close();

            //catch file not found exception
        } catch (FileNotFoundException f) {
            f.printStackTrace();
        }
    }

    /**
     * Logs an info string
     * @param info the info string to be logged
     * @param sourceFile the source code file from which the exception originates
     * @param code the info's code - unique to that source code
     * @param toFile true if the method should also log the info to a log file, should always be true except
     *               when causes infinite loops
     */
    public static void log(String info, String sourceFile, int code, boolean toFile) {

        //get log lines
        String logLine = getLogLine(false, sourceFile, code);

        //remove newline from info if its there
        int infoLength = info.length();
        if (infoLength > 0) {
            if (info.charAt(infoLength - 1) == '\n') {
                info = info.substring(0, infoLength - 1);
            }
        }

        //print to console
        System.out.println(logLine + info);

        //print to file
        if (toFile) {

            //get file line
            String fileName = getFileLine();

            //try to open file
            PrintWriter out;
            try {
                out = new PrintWriter(new FileOutputStream(new File(fileName), true));

                //print info to file and to console
                out.println(logLine + info);
                out.close();

                //catch file not found exception
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return the appropriate file name/directory for a log file at the given date and time
     */
    private static String getFileLine() {

        //compile file line
        Utils.ensureDirectory(LOG_DIRECTORY);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd-yy");
        String fileName = LOG_DIRECTORY + "log " + dateFormatter.format(LocalDateTime.now()) + ".txt";
        return fileName;
    }

    /**
     * Creates an appropriate log line for a thing to be logged
     * @param exception true if the thing being logged is an exception, false otherwise
     * @param sourceFile the source code file from which the event originates
     * @param code the events's code - unique to that source file
     * @return the build log line
     */
    private static String getLogLine(boolean exception, String sourceFile, int code) {

        //compile log line
        String levelLine = exception ? "EXCEPTION" : "INFO";
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String logLine = "[" + timeFormatter.format(LocalDateTime.now()) + "][" + levelLine + "][" + sourceFile + "]" +
                "[code " + code + "]: ";
        return logLine;
    }
}
