
package transport.transport_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import transport.transport_api.entity.Orden;
import transport.transport_api.enums.OrderStatus;
import transport.transport_api.dto.OrderResponse;
import transport.transport_api.dto.UpdateOrderStatusRequest;
import transport.transport_api.exception.InvalidOrderStatusException;
import transport.transport_api.exception.ResourceNotFoundException;
import transport.transport_api.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private UUID orderId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
    }

    @Test
    void whenOrderExists() {

        Orden orden = new Orden();
        orden.setOrigen("CDMX");
        orden.setDestino("Puebla");

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(orden));

        OrderResponse response = orderService.getOrderById(orderId);

        assertEquals("CDMX", response.origin());
        assertEquals("Puebla", response.destination());

        verify(orderRepository).findById(orderId);
    }

    @Test
    void whenOrderDoesNotExist() {

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.getOrderById(orderId));

        assertTrue(
                exception.getMessage().contains(orderId.toString()));

        verify(orderRepository).findById(orderId);
    }
    

@Test
void setOrderToInTransitTest() {
    Orden order = new Orden();
    order.setStatus(OrderStatus.CREATED);

    when(orderRepository.findById(orderId))
            .thenReturn(Optional.of(order));

    OrderResponse response = orderService.updateOrderStatus(
            orderId,
            new UpdateOrderStatusRequest(OrderStatus.IN_TRANSIT)
    );

    assertEquals(OrderStatus.IN_TRANSIT, response.status());
    verify(orderRepository).findById(orderId);
}

@Test
void setOrderToDeliveredTest() {
    Orden order = new Orden ();
    order.setStatus(OrderStatus.IN_TRANSIT);

    when(orderRepository.findById(orderId))
            .thenReturn(Optional.of(order));

    OrderResponse response = orderService.updateOrderStatus(
            orderId,
            new UpdateOrderStatusRequest(OrderStatus.DELIVERED)
    );

    assertEquals(OrderStatus.DELIVERED, response.status());
}

@Test
void setOrderToCancelledTest() {
    Orden order = new Orden();
    order.setStatus(OrderStatus.CREATED);

    when(orderRepository.findById(orderId))
            .thenReturn(Optional.of(order));

    OrderResponse response = orderService.updateOrderStatus(
            orderId,
            new UpdateOrderStatusRequest(OrderStatus.CANCELLED)
    );

    assertEquals(OrderStatus.CANCELLED, response.status());
}

@Test
void RejectInvalidTransitionTest() {
    Orden order = new Orden();
    order.setStatus(OrderStatus.DELIVERED);

    when(orderRepository.findById(orderId))
            .thenReturn(Optional.of(order));

    InvalidOrderStatusException exception = assertThrows(
            InvalidOrderStatusException.class,
            () -> orderService.updateOrderStatus(
                    orderId,
                    new UpdateOrderStatusRequest(OrderStatus.CREATED)
            )
    );

    assertTrue(exception.getMessage()
            .contains("Invalid status transition"));

    assertEquals(OrderStatus.DELIVERED, order.getStatus());
}

@Test
void RejectStatusUpdateWhenOrderDoesNotExist() {
    when(orderRepository.findById(orderId))
            .thenReturn(Optional.empty());

    assertThrows(
            ResourceNotFoundException.class,
            () -> orderService.updateOrderStatus(
                    orderId,
                    new UpdateOrderStatusRequest(OrderStatus.IN_TRANSIT)
            )
    );
}


@Test
void ReturnOrdersWithoutFiltersTest() {

    Orden first = new Orden();
    first.setOrigen("CDMX");
    first.setDestino("Puebla");

    Orden second = new Orden();
    second.setOrigen("Monterrey");
    second.setDestino("Guadalajara");

    when(orderRepository.findAll(
            org.mockito.ArgumentMatchers
                .<Specification<Orden>>any()))
            .thenReturn(List.of(first, second));

    List<OrderResponse> response = orderService.getOrders(
            null, null, null, null, null
    );

    assertEquals(2, response.size());
    assertEquals("CDMX", response.get(0).origin());
    assertEquals("Monterrey", response.get(1).origin());

    verify(orderRepository).findAll(
            org.mockito.ArgumentMatchers
                .<Specification<Orden>>any());
}

@Test
void ReturnEmptyListWhenNoOrdersMatchTest() {

    when(orderRepository.findAll(
            org.mockito.ArgumentMatchers
                .<Specification<Orden>>any()))
            .thenReturn(List.of());

    List<OrderResponse> response = orderService.getOrders(
            OrderStatus.CREATED,
            "CDMX",
            null,
            null,
            null
    );

    assertTrue(response.isEmpty());
}

@Test
void RejectInvalidDateRangeTest() {

    LocalDateTime from =
            LocalDateTime.of(2026, 11, 30, 0, 0);

    LocalDateTime to =
            LocalDateTime.of(2026, 11, 1, 0, 0);

    assertThrows(
            IllegalArgumentException.class,
            () -> orderService.getOrders(
                    null, null, null, from, to)
    );

    verifyNoInteractions(orderRepository);
}

}
