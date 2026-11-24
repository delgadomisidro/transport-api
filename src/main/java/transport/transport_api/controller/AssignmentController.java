
package transport.transport_api.controller;

import java.net.URI;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import transport.transport_api.dto.AssignmentResponse;
import transport.transport_api.dto.CreateAssignmentRequest;
import transport.transport_api.service.AssignmentService;
import java.nio.file.Path;
import java.util.Locale;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/orders/{orderId}/assignments")
@Tag(name = "Assignments", description = "Driver assignment management")
@SecurityRequirement(name = "bearerAuth")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @Operation(summary = "Assign a driver to an order")
    @PostMapping
    public ResponseEntity<AssignmentResponse> assignDriver(
            @PathVariable UUID orderId,
            @Valid @RequestBody CreateAssignmentRequest request) {

        AssignmentResponse response = assignmentService.assignDriver(orderId, request);

        return ResponseEntity
                .created(URI.create(
                        "/api/orders/" + orderId
                                + "/assignments/" + response.id()))
                .body(response);
    }
    @Operation(summary = "Upload PDF and image")  
    @PostMapping(value = "/{assignmentId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AssignmentResponse> uploadAttachments(
            @PathVariable UUID orderId,
            @PathVariable UUID assignmentId,
            @RequestPart("document") MultipartFile document,
            @RequestPart("image") MultipartFile image) {

        return ResponseEntity.ok(
                assignmentService.uploadAttachments(
                        orderId,
                        assignmentId,
                        document,
                        image));
    }
    @Operation(summary = "Download an attachment")
    @GetMapping("/{assignmentId}/attachments/{type}")
    public ResponseEntity<Resource> downloadAttachment(
            @PathVariable UUID orderId,
            @PathVariable UUID assignmentId,
            @PathVariable String type) {

        Path path = assignmentService.downloadAttachment(
                orderId,
                assignmentId,
                type);

        Resource resource = new FileSystemResource(path);

        String fileName = path.getFileName().toString();

        String lowerName = fileName.toLowerCase(Locale.ROOT);

        MediaType mediaType;

        if (lowerName.endsWith(".pdf")) {

            mediaType = MediaType.APPLICATION_PDF;

        } else if (lowerName.endsWith(".png")) {

            mediaType = MediaType.IMAGE_PNG;

        } else {

            mediaType = MediaType.IMAGE_JPEG;
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }
    @Operation(summary = "Get assignment by ID")
    @GetMapping("/{assignmentId}")
    public ResponseEntity<AssignmentResponse> getAssignment(
            @PathVariable UUID orderId,
            @PathVariable UUID assignmentId) {

        return ResponseEntity.ok(
                assignmentService.getAssignment(
                        orderId,
                        assignmentId));
    }

}
