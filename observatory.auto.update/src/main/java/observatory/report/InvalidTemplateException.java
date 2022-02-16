package observatory.report;

public class InvalidTemplateException extends Exception {

    /**
     * 
     */
    public InvalidTemplateException() {
    }

    /**
     * @param message
     */
    public InvalidTemplateException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public InvalidTemplateException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public InvalidTemplateException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public InvalidTemplateException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
