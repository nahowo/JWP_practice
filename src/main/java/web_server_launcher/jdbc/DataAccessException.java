package web_server_launcher.jdbc;

public class DataAccessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DataAccessException() {
        super();
    }

    public DataAccessException(String message, Throwable cause, boolean enableSuppession, boolean writableStackTrace) {
        super(message, cause, enableSuppession, writableStackTrace);
    }

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(Throwable cause) {
        super(cause);
    }
}
