
package transport.transport_api.repository;

import java.sql.Driver;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import transport.transport_api.entity.Conductor;

public interface DriverRepository
        extends JpaRepository<Conductor, UUID> {

    boolean existsByLicenseNumber(String licenseNumber);
    List<Conductor> findByActive(boolean active);
}
