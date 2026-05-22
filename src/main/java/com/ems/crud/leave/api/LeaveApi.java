package com.ems.crud.leave.api;

import com.ems.crud.leave.dto.LeaveApplyRequest;
import com.ems.crud.leave.dto.LeaveResponse;
import com.ems.crud.leave.dto.LeaveReviewRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@Tag(name = "Leave Management", description = "Apply, review, approve, and reject leave requests")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/leaves")
public interface LeaveApi {

	@Operation(summary = "Apply for leave", description = "Submits a new leave request for the logged-in employee.")
	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
	ResponseEntity<LeaveResponse> applyLeave(@Valid @RequestBody LeaveApplyRequest request);

	@Operation(summary = "My leave requests", description = "Returns all leave requests submitted by the logged-in employee.")
	@GetMapping("/me")
	@PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
	ResponseEntity<List<LeaveResponse>> getMyLeaves();

	@Operation(summary = "Pending leave requests", description = "Returns leave requests awaiting approval. Admin or HR only.")
	@GetMapping("/pending")
	@PreAuthorize("hasAnyRole('ADMIN','HR')")
	ResponseEntity<List<LeaveResponse>> getPendingLeaves();

	@Operation(summary = "Approve leave", description = "Approves a pending leave request by ID. Optional review comment. Admin or HR only.")
	@PutMapping("/{id}/approve")
	@PreAuthorize("hasAnyRole('ADMIN','HR')")
	ResponseEntity<LeaveResponse> approveLeave(
			@Parameter(description = "Leave request ID") @PathVariable Long id,
			@Valid @RequestBody(required = false) LeaveReviewRequest request
	);

	@Operation(summary = "Reject leave", description = "Rejects a pending leave request by ID. Optional review comment. Admin or HR only.")
	@PutMapping("/{id}/reject")
	@PreAuthorize("hasAnyRole('ADMIN','HR')")
	ResponseEntity<LeaveResponse> rejectLeave(
			@Parameter(description = "Leave request ID") @PathVariable Long id,
			@Valid @RequestBody(required = false) LeaveReviewRequest request
	);
}
