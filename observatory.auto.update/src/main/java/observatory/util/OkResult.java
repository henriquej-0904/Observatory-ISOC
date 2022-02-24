package observatory.util;

import jakarta.ws.rs.core.Response.Status;

class OkResult<T> implements Result<T> {
    final T result;

    OkResult(T result) {
        this.result = result;
    }

    public boolean isOK() {
        return true;
    }

    public T value() {
        return this.result;
    }

    public Status error() {
        if (this.result == null) {
            return Status.NO_CONTENT;
        }
        
        return Status.OK;
    }

    public String toString() {
        return "(OK, " + value() + ")";
    }
}
