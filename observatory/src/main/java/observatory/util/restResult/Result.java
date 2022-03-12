package observatory.util.restResult;

import jakarta.ws.rs.core.Response.Status;

public interface Result<T> {
    boolean isOK();

    T value();

    Status error();

    static <T> Result<T> ok(T result) {
        return new OkResult<T>(result);
    }

    static <T> OkResult<T> ok() {
        return new OkResult<T>(null);
    }

    static <T> ErrorResult<T> error(Status error) {
        return new ErrorResult<T>(error);
    }
}
