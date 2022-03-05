package observatory.tests.collection;

public class InvalidResultsFileException extends Exception {

    /**
     * @param message
     */
    public InvalidResultsFileException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public InvalidResultsFileException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public InvalidResultsFileException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
