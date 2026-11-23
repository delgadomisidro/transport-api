
package transport.transport_api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import transport.transport_api.dto.CreateOrderRequest;
import transport.transport_api.dto.OrderResponse;
import transport.transport_api.entity.Orden;
import transport.transport_api.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderResponse createOrder(
            CreateOrderRequest request) {

        Orden orden = new Orden();

        orden.setOrigen(request.origin());
        orden.setDestino(request.destination());

        Orden savedOrder = orderRepository.save(orden);

        return OrderResponse.from(savedOrder);
    }
}
