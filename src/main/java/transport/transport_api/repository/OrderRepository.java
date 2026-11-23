
package transport.transport_api.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import transport.transport_api.entity.Orden;

public interface OrderRepository
        extends JpaRepository<Orden, UUID> {
}
