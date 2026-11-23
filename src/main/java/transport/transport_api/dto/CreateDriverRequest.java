
package transport.transport_api.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateDriverRequest(

    @NotBlank(message = "Driver name is required")
    String name,

    @NotBlank(message = "License number is required")
    String licenseNumber

) {}
