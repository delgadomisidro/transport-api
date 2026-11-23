
package transport.transport_api.controller;

import java.net.URI;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import transport.transport_api.dto.CreateOrderRequest;
import transport.transport_api.dto.OrderResponse;
import transport.transport_api.dto.UpdateOrderStatusRequest;
import transport.transport_api.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    /*
     * Creates a new order
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        OrderResponse response = orderService.createOrder(request);

        URI location = URI.create("/api/orders/" + response.id());

        return ResponseEntity
                .created(location)
                .body(response);
    }
    /*
     * Gets order by id
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable UUID id) {

        OrderResponse response = orderService.getOrderById(id);

        return ResponseEntity.ok(response);
    }
    /*
     * Updates the status of an order
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        OrderResponse response = orderService.updateOrderStatus(id, request);

        return ResponseEntity.ok(response);
    }

}
