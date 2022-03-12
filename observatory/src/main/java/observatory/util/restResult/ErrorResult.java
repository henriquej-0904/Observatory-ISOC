package observatory.util.restResult;

import jakarta.ws.rs.core.Response.Status;

/**
 * @author Henrique Campos Ferreira
 */
class ErrorResult<T> implements Result<T> {
    final Status error;

    ErrorResult(Status error) {
        this.error = error;
    }

    public boolean isOK() {
        return false;
    }

    public T value() {
        throw new RuntimeException("Attempting to extract the value of an Error: " + error());
    }

    public Status error() {
        return this.error;
    }

    public String toString() {
        return "(" + error() + ")";
    }
}
