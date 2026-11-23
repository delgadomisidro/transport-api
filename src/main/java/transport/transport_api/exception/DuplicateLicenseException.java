package transport.transport_api.exception;

public class DuplicateLicenseException extends RuntimeException {

    public DuplicateLicenseException(String message) {
        super(message);
    }
}