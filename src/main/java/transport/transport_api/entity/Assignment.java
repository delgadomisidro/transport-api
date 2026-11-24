
package transport.transport_api.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(
    name = "assignments",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_assignment_order",
        columnNames = "order_id"
    )
)
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Orden order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "driver_id", nullable = false)
    private Conductor driver;

    @Column(nullable = false, updatable = false)
    private LocalDateTime assignedAt;

    @Column(name = "document_path")
    private String documentPath;

    @Column(name = "image_path")
    private String imagePath;

    @PrePersist
    protected void onCreate() {
        assignedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public Orden getOrder() {
        return order;
    }

    public void setOrder(Orden order) {
        this.order = order;
    }

    public Conductor getDriver() {
        return driver;
    }

    public void setDriver(Conductor driver) {
        this.driver = driver;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
