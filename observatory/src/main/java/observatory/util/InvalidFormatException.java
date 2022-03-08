package observatory.util;

public class InvalidFormatException extends Exception {

    /**
     * @param message
     */
    public InvalidFormatException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public InvalidFormatException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public InvalidFormatException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
