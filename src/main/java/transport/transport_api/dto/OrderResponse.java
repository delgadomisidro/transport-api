
package transport.transport_api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import transport.transport_api.entity.Orden;
import transport.transport_api.enums.OrderStatus;

public record OrderResponse(
    UUID id,
    OrderStatus status,
    String origin,
    String destination,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static OrderResponse from(Orden orden) {
        return new OrderResponse(
            orden.getId(),
            orden.getStatus(),
            orden.getOrigen(),
            orden.getDestino(),
            orden.getCreatedAt(),
            orden.getUpdatedAt()
        );
    }
}
