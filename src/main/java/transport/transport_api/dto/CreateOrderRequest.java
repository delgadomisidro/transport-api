
package transport.transport_api.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateOrderRequest(

    @NotBlank(message = "Origin is required")
    String origin,

    @NotBlank(message = "Destination is required")
    String destination

) {}

