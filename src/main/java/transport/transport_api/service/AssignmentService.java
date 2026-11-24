
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
import java.nio.file.Path;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class AssignmentService {

        private final AssignmentRepository assignmentRepository;
        private final OrderRepository orderRepository;
        private final DriverRepository driverRepository;
        private final FileStorageService fileStorageService;

        public AssignmentService(
                        AssignmentRepository assignmentRepository,
                        OrderRepository orderRepository,
                        DriverRepository driverRepository,
                        FileStorageService fileStorageService) {

                this.assignmentRepository = assignmentRepository;
                this.orderRepository = orderRepository;
                this.driverRepository = driverRepository;
                this.fileStorageService = fileStorageService;
        }

        @Transactional
        public AssignmentResponse assignDriver(
                        UUID orderId,
                        CreateAssignmentRequest request) {

                Orden order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Order not found with id: " + orderId));

                Conductor driver = driverRepository.findById(request.conductorId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Conductor not found with id: " + request.conductorId()));

                if (order.getStatus() != OrderStatus.CREATED) {
                        throw new InvalidAssignmentException(
                                        "Only CREATED orders can be assigned");
                }

                if (!driver.isActive()) {
                        throw new InvalidAssignmentException(
                                        "Cannot assign an inactive driver");
                }

                if (assignmentRepository.existsByOrderId(orderId)) {
                        throw new InvalidAssignmentException(
                                        "Order already has an assigned driver");
                }

                Assignment assignment = new Assignment();
                assignment.setOrder(order);
                assignment.setDriver(driver);

                Assignment saved = assignmentRepository.save(assignment);

                return AssignmentResponse.from(saved);
        }

        private Assignment findAssignment(
                        UUID orderId,
                        UUID assignmentId) {

                Assignment assignment = assignmentRepository
                                .findById(assignmentId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Assignment not found"));

                if (!assignment.getOrder().getId().equals(orderId)) {
                        throw new ResourceNotFoundException(
                                        "Assignment does not belong to this order");
                }

                return assignment;
        }

        @Transactional
        public AssignmentResponse uploadAttachments(
                        UUID orderId,
                        UUID assignmentId,
                        MultipartFile document,
                        MultipartFile image) {

                Assignment assignment = findAssignment(orderId, assignmentId);

                if (assignment.getDocumentPath() != null
                                || assignment.getImagePath() != null) {

                        throw new InvalidAssignmentException(
                                        "Assignment already has attachments");
                }

                // Validate both files before storing them
                fileStorageService.validate(document, "document");
                fileStorageService.validate(image, "image");

                String documentPath = fileStorageService.store(document, "document");

                // Delete the document if the transaction fails
                registerRollbackCleanup(documentPath);

                String imagePath = fileStorageService.store(image, "image");

                // Delete the image if the transaction fails
                registerRollbackCleanup(imagePath);

                assignment.setDocumentPath(documentPath);
                assignment.setImagePath(imagePath);

                assignmentRepository.saveAndFlush(assignment);

                return AssignmentResponse.from(assignment);
        }

        private void registerRollbackCleanup(String fileName) {

                TransactionSynchronizationManager
                                .registerSynchronization(
                                                new TransactionSynchronization() {

                                                        @Override
                                                        public void afterCompletion(int status) {

                                                                if (status == STATUS_ROLLED_BACK) {

                                                                        try {
                                                                                fileStorageService.delete(fileName);

                                                                        } catch (RuntimeException ex) {

                                                                                org.slf4j.LoggerFactory
                                                                                                .getLogger(AssignmentService.class)
                                                                                                .error(
                                                                                                                "Failed to clean up attachment {}",
                                                                                                                fileName,
                                                                                                                ex);
                                                                        }
                                                                }
                                                        }
                                                });
        }

        @Transactional(readOnly = true)
        public Path downloadAttachment(
                        UUID orderId,
                        UUID assignmentId,
                        String type) {

                Assignment assignment = findAssignment(orderId, assignmentId);

                String fileName = switch (type) {

                        case "document" ->
                                assignment.getDocumentPath();

                        case "image" ->
                                assignment.getImagePath();

                        default ->
                                throw new IllegalArgumentException(
                                                "Invalid attachment type");
                };

                return fileStorageService.load(fileName);
        }

        @Transactional(readOnly = true)
        public AssignmentResponse getAssignment(
                        UUID orderId,
                        UUID assignmentId) {

                return AssignmentResponse.from(
                                findAssignment(orderId, assignmentId));
        }

}
