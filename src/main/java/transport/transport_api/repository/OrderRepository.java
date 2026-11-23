
package transport.transport_api.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import transport.transport_api.entity.Orden;

public interface OrderRepository
        extends JpaRepository<Orden, UUID>,
        JpaSpecificationExecutor<Orden> {
}
