
package transport.transport_api.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import transport.transport_api.dto.AssignmentResponse;
import transport.transport_api.dto.CreateAssignmentRequest;
import transport.transport_api.entity.Assignment;
import transport.transport_api.entity.Conductor;
import transport.transport_api.entity.Orden;
import transport.transport_api.enums.OrderStatus;
import transport.transport_api.exception.InvalidAssignmentException;
import transport.transport_api.exception.ResourceNotFoundException;
import transport.transport_api.repository.AssignmentRepository;
import transport.transport_api.repository.DriverRepository;
import transport.transport_api.repository.OrderRepository;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final OrderRepository orderRepository;
    private final DriverRepository driverRepository;

    public AssignmentService(
            AssignmentRepository assignmentRepository,
            OrderRepository orderRepository,
            DriverRepository driverRepository) {

        this.assignmentRepository = assignmentRepository;
        this.orderRepository = orderRepository;
        this.driverRepository = driverRepository;
    }

    @Transactional
    public AssignmentResponse assignDriver(
            UUID orderId,
            CreateAssignmentRequest request) {

        Orden order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + orderId
                ));

        Conductor driver = driverRepository.findById(request.conductorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Conductor not found with id: " + request.conductorId()
                ));

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new InvalidAssignmentException(
                    "Only CREATED orders can be assigned"
            );
        }

        if (!driver.isActive()) {
            throw new InvalidAssignmentException(
                    "Cannot assign an inactive driver"
            );
        }

        if (assignmentRepository.existsByOrderId(orderId)) {
            throw new InvalidAssignmentException(
                    "Order already has an assigned driver"
            );
        }

        Assignment assignment = new Assignment();
        assignment.setOrder(order);
        assignment.setDriver(driver);

        Assignment saved = assignmentRepository.save(assignment);

        return AssignmentResponse.from(saved);
    }
}
