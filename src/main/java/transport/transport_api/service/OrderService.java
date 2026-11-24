
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
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import transport.transport_api.repository.specification.OrderSpecification;

import transport.transport_api.exception.InvalidOrderStatusException;
import transport.transport_api.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

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
        log.atInfo()
                .addKeyValue("event", "order_created")
                .addKeyValue("orderId", savedOrder.getId())
                .addKeyValue("status", savedOrder.getStatus())
                .log("Order created successfully");

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
        log.atInfo()
                .addKeyValue("event", "order_status_updated")
                .addKeyValue("orderId", orden.getId())
                .addKeyValue("newStatus", orden.getStatus())
                .log("Order status updated");

        return OrderResponse.from(orden);
    }

    /**
     * Gets all orders matching the given criteria
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrders(
            OrderStatus status,
            String origin,
            String destination,
            LocalDateTime from,
            LocalDateTime to) {

        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException(
                    "From date must not be after to date");
        }

        Specification<Orden> specification = Specification.where(
                OrderSpecification.hasStatus(status))
                .and(OrderSpecification.hasOrigin(origin))
                .and(OrderSpecification.hasDestination(destination))
                .and(OrderSpecification.createdAfter(from))
                .and(OrderSpecification.createdBefore(to));

        return orderRepository.findAll(specification)
                .stream()
                .map(OrderResponse::from)
                .toList();
    }

}
