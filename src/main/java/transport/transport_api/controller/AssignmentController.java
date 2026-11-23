
package transport.transport_api.controller;

import java.net.URI;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import transport.transport_api.dto.AssignmentResponse;
import transport.transport_api.dto.CreateAssignmentRequest;
import transport.transport_api.service.AssignmentService;

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
}
