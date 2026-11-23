
package transport.transport_api.exception;

import java.time.Instant;
import java.util.Map;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            ResourceNotFoundException ex) {

        Map<String, Object> error = Map.of(
                "timestamp", Instant.now().toString(),
                "status", 404,
                "error", "Not Found",
                "message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(InvalidOrderStatusException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidStatus(
            InvalidOrderStatusException ex) {

        Map<String, Object> error = Map.of(
                "timestamp", Instant.now().toString(),
                "status", 409,
                "error", "Conflict",
                "message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }


@ExceptionHandler(OptimisticLockingFailureException.class)
public ResponseEntity<Map<String, Object>> handleConcurrencyConflict(
        OptimisticLockingFailureException ex) {

    Map<String, Object> error = Map.of(
        "timestamp", Instant.now().toString(),
        "status", 409,
        "error", "Conflict",
        "message", "Order was modified by another transaction"
    );

    return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(error);
}

@ExceptionHandler(IllegalArgumentException.class)
public ResponseEntity<Map<String, Object>> handleInvalidArgument(
        IllegalArgumentException ex) {

    Map<String, Object> error = Map.of(
        "timestamp", Instant.now().toString(),
        "status", 400,
        "error", "Bad Request",
        "message", ex.getMessage()
    );

    return ResponseEntity
            .badRequest()
            .body(error);
}


@ExceptionHandler(DuplicateLicenseException.class)
public ResponseEntity<Map<String, Object>> handleDuplicateLicense(
        DuplicateLicenseException ex) {

    return ResponseEntity.status(HttpStatus.CONFLICT).body(
            Map.of(
                "timestamp", Instant.now().toString(),
                "status", 409,
                "error", "Conflict",
                "message", ex.getMessage()
            )
    );
}

@ExceptionHandler(InvalidAssignmentException.class)
public ResponseEntity<Map<String, Object>> handleInvalidAssignment(
        InvalidAssignmentException ex) {

    return ResponseEntity.status(HttpStatus.CONFLICT).body(
            Map.of(
                "timestamp", Instant.now().toString(),
                "status", 409,
                "error", "Conflict",
                "message", ex.getMessage()
            )
    );
}

}