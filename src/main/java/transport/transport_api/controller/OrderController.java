
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
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import transport.transport_api.enums.OrderStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;



@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Transport order management")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /*
     * Creates a new order
     */
    @Operation(summary = "Create a new order")
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
    @Operation(summary = "Get order by ID")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable UUID id) {

        OrderResponse response = orderService.getOrderById(id);

        return ResponseEntity.ok(response);
    }

    /*
     * Updates the status of an order
     */
    @Operation(summary = "Update order status")
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        OrderResponse response = orderService.updateOrderStatus(id, request);

        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get orders")
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        return ResponseEntity.ok(
                orderService.getOrders(
                        status, origin, destination, from, to));
    }

}
