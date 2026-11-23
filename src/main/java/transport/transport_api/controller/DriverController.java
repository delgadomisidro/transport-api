
package transport.transport_api.controller;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import transport.transport_api.dto.CreateDriverRequest;
import transport.transport_api.dto.DriverResponse;
import transport.transport_api.service.DriverService;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping
    public ResponseEntity<DriverResponse> createDriver(
            @Valid @RequestBody CreateDriverRequest request) {

        DriverResponse response = driverService.createDriver(request);

        return ResponseEntity
                .created(URI.create("/api/drivers/" + response.id()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<DriverResponse>> getDrivers(
            @RequestParam(required = false) Boolean active) {

        return ResponseEntity.ok(
                driverService.getDrivers(active));
    }
}
