
package transport.transport_api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import transport.transport_api.dto.CreateOrderRequest;
import transport.transport_api.dto.OrderResponse;
import transport.transport_api.dto.UpdateOrderStatusRequest;
import transport.transport_api.entity.Orden;
import transport.transport_api.enums.OrderStatus;
import transport.transport_api.repository.OrderRepository;
import java.util.UUID;

import transport.transport_api.exception.InvalidOrderStatusException;
import transport.transport_api.exception.ResourceNotFoundException;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    /**
     * Creates a new order
     */
    @Transactional
    public OrderResponse createOrder(
            CreateOrderRequest request) {

        Orden orden = new Orden();

        orden.setOrigen(request.origin());
        orden.setDestino(request.destination());

        Orden savedOrder = orderRepository.save(orden);

        return OrderResponse.from(savedOrder);
    }

    /**
     * Gets order by id or throws ResourceNotFoundException
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID id) {

        Orden orden = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + id));

        return OrderResponse.from(orden);
    }

    /**
     * Updates the status of an order
     */
    @Transactional
    public OrderResponse updateOrderStatus(
            UUID id,
            UpdateOrderStatusRequest request) {

        Orden orden = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + id));

        OrderStatus current = orden.getStatus();
        OrderStatus next = request.status();

        boolean validTransition = switch (current) {
            case CREATED ->
                next == OrderStatus.IN_TRANSIT
                        || next == OrderStatus.CANCELLED;

            case IN_TRANSIT ->
                next == OrderStatus.DELIVERED;

            case DELIVERED, CANCELLED -> false;
        };

        if (!validTransition) {
            throw new InvalidOrderStatusException(
                    "Invalid status transition from "
                            + current + " to " + next);
        }

        orden.setStatus(next);

        return OrderResponse.from(orden);
    }

}
