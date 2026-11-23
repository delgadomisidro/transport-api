
package transport.transport_api.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import transport.transport_api.entity.Assignment;

public interface AssignmentRepository
        extends JpaRepository<Assignment, UUID> {

    boolean existsByOrderId(UUID orderId);
}
