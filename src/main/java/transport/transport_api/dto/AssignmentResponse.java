
package transport.transport_api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import transport.transport_api.entity.Assignment;

public record AssignmentResponse(
        UUID id,
        UUID orderId,
        UUID driverId,
        String driverName,
        LocalDateTime assignedAt
) {

    public static AssignmentResponse from(Assignment assignment) {
        return new AssignmentResponse(
                assignment.getId(),
                assignment.getOrder().getId(),
                assignment.getDriver().getId(),
                assignment.getDriver().getName(),
                assignment.getAssignedAt()
        );
    }
}
