
package transport.transport_api.dto;

import java.util.UUID;

import transport.transport_api.entity.Conductor;


public record DriverResponse(
        UUID id,
        String name,
        String licenseNumber,
        boolean active
) {

    public static DriverResponse from(Conductor driver) {
        return new DriverResponse(
                driver.getId(),
                driver.getName(),
                driver.getLicenseNumber(),
                driver.isActive()
        );
    }
}
