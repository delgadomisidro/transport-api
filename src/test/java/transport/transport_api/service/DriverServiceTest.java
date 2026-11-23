
package transport.transport_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import transport.transport_api.dto.CreateDriverRequest;
import transport.transport_api.dto.DriverResponse;
import transport.transport_api.entity.Conductor;
import transport.transport_api.exception.DuplicateLicenseException;
import transport.transport_api.repository.DriverRepository;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private DriverService driverService;

    @Test
    void shouldCreateDriver() {
        CreateDriverRequest request =
                new CreateDriverRequest("Juan", "LIC-001");

        when(driverRepository.existsByLicenseNumber("LIC-001"))
                .thenReturn(false);

        when(driverRepository.save(any(Conductor.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        DriverResponse response =
                driverService.createDriver(request);

        assertEquals("Juan", response.name());
        assertEquals("LIC-001", response.licenseNumber());
        assertTrue(response.active());

        verify(driverRepository).save(any(Conductor.class));
    }

    @Test
    void shouldRejectDuplicateLicense() {
        CreateDriverRequest request =
                new CreateDriverRequest("Juan", "LIC-001");

        when(driverRepository.existsByLicenseNumber("LIC-001"))
                .thenReturn(true);

        assertThrows(
                DuplicateLicenseException.class,
                () -> driverService.createDriver(request)
        );

        verify(driverRepository, never())
                .save(any(Conductor.class));
    }

    @Test
    void shouldReturnOnlyActiveDrivers() {
        Conductor conductor = new Conductor();
        conductor.setName("Juan");
        conductor.setLicenseNumber("LIC-001");
        conductor.setActive(true);

        when(driverRepository.findByActive(true))
                .thenReturn(List.of(conductor));

        List<DriverResponse> response =
                driverService.getDrivers(true);

        assertEquals(1, response.size());
        assertTrue(response.get(0).active());

        verify(driverRepository).findByActive(true);
        verify(driverRepository, never()).findAll();
    }

    @Test
    void shouldReturnAllDriversWhenFilterIsNull() {
        Conductor active = new Conductor();
        active.setName("Juan");
        active.setActive(true);

        Conductor inactive = new Conductor();
        inactive.setName("Pedro");
        inactive.setActive(false);

        when(driverRepository.findAll())
                .thenReturn(List.of(active, inactive));

        List<DriverResponse> response =
                driverService.getDrivers(null);

        assertEquals(2, response.size());
        verify(driverRepository).findAll();
    }
}
