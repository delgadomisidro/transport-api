
package transport.transport_api.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record CreateAssignmentRequest(

    @NotNull(message = "Conductor ID is required")
    UUID conductorId

) {}
