package observatory.internetnlAPI;

/**
 * Represents an error while executing an operation in the Internetnl API.
 */
public class InternetnlAPIException extends Exception
{

    /**
     * @param message
     */
    public InternetnlAPIException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public InternetnlAPIException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public InternetnlAPIException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public InternetnlAPIException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
