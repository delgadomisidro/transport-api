
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

@RestController
@RequestMapping("/api/orders/{orderId}/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping
    public ResponseEntity<AssignmentResponse> assignDriver(
            @PathVariable UUID orderId,
            @Valid @RequestBody CreateAssignmentRequest request) {

        AssignmentResponse response =
                assignmentService.assignDriver(orderId, request);

        return ResponseEntity
                .created(URI.create(
                        "/api/orders/" + orderId
                        + "/assignments/" + response.id()
                ))
                .body(response);
    }

@PostMapping(
    value = "/{assignmentId}/attachments",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
)
public ResponseEntity<AssignmentResponse> uploadAttachments(
        @PathVariable UUID orderId,
        @PathVariable UUID assignmentId,
        @RequestParam("document") MultipartFile document,
        @RequestParam("image") MultipartFile image) {

    return ResponseEntity.ok(
        assignmentService.uploadAttachments(
            orderId,
            assignmentId,
            document,
            image
        )
    );
}

@GetMapping("/{assignmentId}/attachments/{type}")
public ResponseEntity<Resource> downloadAttachment(
        @PathVariable UUID orderId,
        @PathVariable UUID assignmentId,
        @PathVariable String type) {

    Path path = assignmentService.downloadAttachment(
        orderId,
        assignmentId,
        type
    );

    Resource resource = new FileSystemResource(path);

    String fileName = path.getFileName().toString();

    String lowerName =
            fileName.toLowerCase(Locale.ROOT);

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
            "attachment; filename=\"" + fileName + "\""
        )
        .body(resource);
}

@GetMapping("/{assignmentId}")
public ResponseEntity<AssignmentResponse> getAssignment(
        @PathVariable UUID orderId,
        @PathVariable UUID assignmentId) {

    return ResponseEntity.ok(
        assignmentService.getAssignment(
            orderId,
            assignmentId
        )
    );
}


}
