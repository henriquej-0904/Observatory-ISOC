package observatory.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Provides functions to configure loggers.
 * 
 * @author Henrique Campos Ferreira
 */
public class Logging
{
    private static final SimpleDateFormat FILE_NAME_DATE_FORMAT = new SimpleDateFormat("yyyy_MM_dd.HH-mm-ss");

    /**
     * Configure a logger to output to a file.
     * 
     * @param logger - The logger to configure.
     * @param logFile - The log file.
     * @param append - True to append to the log file.
     * @return The configured logger.
     * @throws IOException
     */
    public static Logger configLogger(Logger logger, File logFile, boolean append) throws IOException
    {
        FileHandler logFileHandler = new FileHandler(logFile.getCanonicalPath(), append);
        logFileHandler.setFormatter(new SimpleFormatter());

        for (Handler handler : logger.getHandlers()) {
            logger.removeHandler(handler);
        }

        logger.addHandler(logFileHandler);
        logger.setUseParentHandlers(false);
        return logger;
    }

    /**
     * Create a log file name.
     * 
     * @param folder - The directory of the log file.
     * @param name - The name of the log file.
     * @return The generated log file.
     */
    public static File createLogFileName(File folder, String name)
    {
        Calendar calendar = Calendar.getInstance();
        return new File(folder,
            String.format("%s.%s.log", FILE_NAME_DATE_FORMAT.format(calendar.getTime()), name));
    }
}
