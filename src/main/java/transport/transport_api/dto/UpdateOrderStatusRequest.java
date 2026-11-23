
package transport.transport_api.dto;

import jakarta.validation.constraints.NotNull;
import transport.transport_api.enums.OrderStatus;

public record UpdateOrderStatusRequest(
    @NotNull(message = "Status is required")
    OrderStatus status
) {}
