package ar.edu.utn.dds.k3003.exceptions.base;

public abstract class AppException extends RuntimeException {
    private final ErrorCode code;

    protected AppException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    protected AppException(ErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public ErrorCode getCode() {
        return code;
    }
}
