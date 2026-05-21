package com.ems.crud.leave;

import com.ems.crud.leave.dto.LeaveApplyRequest;
import com.ems.crud.leave.dto.LeaveResponse;
import com.ems.crud.leave.dto.LeaveReviewRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/leaves")
@Tag(name = "Leave Management", description = "Apply, approve, and reject leave requests")
@SecurityRequirement(name = "bearerAuth")
public class LeaveController {

	private final LeaveService leaveService;

	public LeaveController(LeaveService leaveService) {
		this.leaveService = leaveService;
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
	public ResponseEntity<LeaveResponse> applyLeave(@Valid @RequestBody LeaveApplyRequest request) {
		return ResponseEntity.ok(leaveService.applyLeave(request));
	}

	@GetMapping("/me")
	@PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
	public ResponseEntity<List<LeaveResponse>> getMyLeaves() {
		return ResponseEntity.ok(leaveService.getMyLeaves());
	}

	@GetMapping("/pending")
	@PreAuthorize("hasAnyRole('ADMIN','HR')")
	public ResponseEntity<List<LeaveResponse>> getPendingLeaves() {
		return ResponseEntity.ok(leaveService.getPendingLeaves());
	}

	@PutMapping("/{id}/approve")
	@PreAuthorize("hasAnyRole('ADMIN','HR')")
	public ResponseEntity<LeaveResponse> approveLeave(
			@PathVariable Long id,
			@Valid @RequestBody(required = false) LeaveReviewRequest request
	) {
		LeaveReviewRequest reviewRequest = request != null ? request : new LeaveReviewRequest(null);
		return ResponseEntity.ok(leaveService.approveLeave(id, reviewRequest));
	}

	@PutMapping("/{id}/reject")
	@PreAuthorize("hasAnyRole('ADMIN','HR')")
	public ResponseEntity<LeaveResponse> rejectLeave(
			@PathVariable Long id,
			@Valid @RequestBody(required = false) LeaveReviewRequest request
	) {
		LeaveReviewRequest reviewRequest = request != null ? request : new LeaveReviewRequest(null);
		return ResponseEntity.ok(leaveService.rejectLeave(id, reviewRequest));
	}
}
