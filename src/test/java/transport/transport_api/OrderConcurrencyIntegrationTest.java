
package transport.transport_api;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import transport.transport_api.entity.Orden;
import transport.transport_api.enums.OrderStatus;
import transport.transport_api.repository.OrderRepository;

@SpringBootTest
class OrderConcurrencyIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void shouldPreventLostUpdate() {

        TransactionTemplate tx =
                new TransactionTemplate(transactionManager);

        // 1. Crear una orden inicial
        UUID orderId = tx.execute(status -> {
            Orden order = new Orden();
            order.setOrigen("CDMX");
            order.setDestino("Puebla");
            return orderRepository.saveAndFlush(order).getId();
        });

        assertNotNull(orderId);

        // 2. Dos transacciones leen la misma versión
        Orden firstCopy = tx.execute(status ->
                orderRepository.findById(orderId).orElseThrow());

        Orden secondCopy = tx.execute(status ->
                orderRepository.findById(orderId).orElseThrow());

        assertEquals(firstCopy.getVersion(),
                     secondCopy.getVersion());

        // 3. La primera copia se actualiza
        tx.executeWithoutResult(status -> {
            firstCopy.setStatus(OrderStatus.IN_TRANSIT);
            orderRepository.saveAndFlush(firstCopy);
        });

        // 4. La segunda copia intenta sobrescribirla
        assertThrows(
                ObjectOptimisticLockingFailureException.class,
                () -> tx.executeWithoutResult(status -> {
                    secondCopy.setStatus(OrderStatus.CANCELLED);
                    orderRepository.saveAndFlush(secondCopy);
                })
        );

        // 5. Confirmar que el primer cambio permanece
        Orden persisted = tx.execute(status ->
                orderRepository.findById(orderId).orElseThrow());

        assertNotNull(persisted);
        assertEquals(OrderStatus.IN_TRANSIT,
                     persisted.getStatus());
    }
}
