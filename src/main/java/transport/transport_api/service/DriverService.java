
package transport.transport_api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import transport.transport_api.dto.CreateDriverRequest;
import transport.transport_api.dto.DriverResponse;
import transport.transport_api.entity.Conductor;
import transport.transport_api.exception.DuplicateLicenseException;
import transport.transport_api.repository.DriverRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DriverService {

    private final DriverRepository driverRepository;
    private static final Logger log = LoggerFactory.getLogger(DriverService.class);

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Transactional
    public DriverResponse createDriver(CreateDriverRequest request) {

        if (driverRepository.existsByLicenseNumber(
                request.licenseNumber())) {

            throw new DuplicateLicenseException(
                    "Driver license already exists: "
                            + request.licenseNumber());
        }

        Conductor driver = new Conductor();
        driver.setName(request.name());
        driver.setLicenseNumber(request.licenseNumber());
        driver.setActive(true);

        Conductor saved = driverRepository.save(driver);
        log.atInfo()
                .addKeyValue("event", "driver_created")
                .addKeyValue("driverId", saved.getId())
                .log("Driver created successfully");

        return DriverResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<DriverResponse> getDrivers(Boolean active) {

        List<Conductor> drivers;

        if (active == null) {
            drivers = driverRepository.findAll();
        } else {
            drivers = driverRepository.findByActive(active);
        }

        return drivers.stream()
                .map(DriverResponse::from)
                .toList();
    }

}
