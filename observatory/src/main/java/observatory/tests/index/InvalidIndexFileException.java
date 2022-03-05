package observatory.tests.index;

/**
 * Thrown when the provided index file is invalid (i.e. does not exist, invalid format, etc.).
 * This is a wrapper exception for any error while reading an index file.
 */
public class InvalidIndexFileException extends Exception
{

    /**
     * @param message
     */
    public InvalidIndexFileException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public InvalidIndexFileException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public InvalidIndexFileException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
