package com.ems.crud.leave;

import com.ems.crud.leave.api.LeaveApi;
import com.ems.crud.leave.dto.LeaveApplyRequest;
import com.ems.crud.leave.dto.LeaveResponse;
import com.ems.crud.leave.dto.LeaveReviewRequest;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LeaveController implements LeaveApi {

	private final LeaveService leaveService;

	public LeaveController(LeaveService leaveService) {
		this.leaveService = leaveService;
	}

	@Override
	public ResponseEntity<LeaveResponse> applyLeave(LeaveApplyRequest request) {
		return ResponseEntity.ok(leaveService.applyLeave(request));
	}

	@Override
	public ResponseEntity<List<LeaveResponse>> getMyLeaves() {
		return ResponseEntity.ok(leaveService.getMyLeaves());
	}

	@Override
	public ResponseEntity<List<LeaveResponse>> getPendingLeaves() {
		return ResponseEntity.ok(leaveService.getPendingLeaves());
	}

	@Override
	public ResponseEntity<LeaveResponse> approveLeave(Long id, LeaveReviewRequest request) {
		LeaveReviewRequest reviewRequest = request != null ? request : new LeaveReviewRequest(null);
		return ResponseEntity.ok(leaveService.approveLeave(id, reviewRequest));
	}

	@Override
	public ResponseEntity<LeaveResponse> rejectLeave(Long id, LeaveReviewRequest request) {
		LeaveReviewRequest reviewRequest = request != null ? request : new LeaveReviewRequest(null);
		return ResponseEntity.ok(leaveService.rejectLeave(id, reviewRequest));
	}
}
