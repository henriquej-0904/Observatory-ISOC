package observatory.argsParser;

public class ParserException extends Exception {

    /**
     * @param message
     */
    public ParserException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public ParserException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public ParserException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
