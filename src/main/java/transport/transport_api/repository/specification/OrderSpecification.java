
package transport.transport_api.repository.specification;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import transport.transport_api.entity.Orden;
import transport.transport_api.enums.OrderStatus;

public final class OrderSpecification {

    private OrderSpecification() {
    }

    public static Specification<Orden> hasStatus(
            OrderStatus status) {

        return (root, query, cb) ->
                status == null
                        ? cb.conjunction()
                        : cb.equal(root.get("status"), status);
    }

    public static Specification<Orden> hasOrigin(
            String origin) {

        return (root, query, cb) ->
                origin == null || origin.isBlank()
                        ? cb.conjunction()
                        : cb.like(
                            cb.lower(root.get("origin")),
                            "%" + origin.toLowerCase() + "%"
                        );
    }

    public static Specification<Orden> hasDestination(
            String destination) {

        return (root, query, cb) ->
                destination == null || destination.isBlank()
                        ? cb.conjunction()
                        : cb.like(
                            cb.lower(root.get("destination")),
                            "%" + destination.toLowerCase() + "%"
                        );
    }

    public static Specification<Orden> createdAfter(
            LocalDateTime from) {

        return (root, query, cb) ->
                from == null
                        ? cb.conjunction()
                        : cb.greaterThanOrEqualTo(
                            root.get("createdAt"), from
                        );
    }

    public static Specification<Orden> createdBefore(
            LocalDateTime to) {

        return (root, query, cb) ->
                to == null
                        ? cb.conjunction()
                        : cb.lessThanOrEqualTo(
                            root.get("createdAt"), to
                        );
    }
}
