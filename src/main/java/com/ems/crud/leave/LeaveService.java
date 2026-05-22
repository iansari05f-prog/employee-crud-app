package com.ems.crud.leave;

import com.ems.crud.auth.User;
import com.ems.crud.common.LeaveStatus;
import com.ems.crud.common.Role;
import com.ems.crud.employee.Employee;
import com.ems.crud.exception.BadRequestException;
import com.ems.crud.exception.ForbiddenException;
import com.ems.crud.exception.ResourceNotFoundException;
import com.ems.crud.leave.dto.LeaveApplyRequest;
import com.ems.crud.leave.dto.LeaveResponse;
import com.ems.crud.leave.dto.LeaveReviewRequest;
import com.ems.crud.security.SecurityUtils;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LeaveService {

	private final LeaveRepository leaveRepository;
	private final SecurityUtils securityUtils;

	public LeaveService(LeaveRepository leaveRepository, SecurityUtils securityUtils) {
		this.leaveRepository = leaveRepository;
		this.securityUtils = securityUtils;
	}

	@Transactional
	public LeaveResponse applyLeave(LeaveApplyRequest request) {
		Employee employee = getCurrentEmployee();

		if (request.endDate().isBefore(request.startDate())) {
			throw new BadRequestException("End date cannot be before start date");
		}

		LeaveRequest leaveRequest = new LeaveRequest(
				employee,
				request.leaveType(),
				request.startDate(),
				request.endDate(),
				request.reason().trim()
		);

		return toResponse(leaveRepository.save(leaveRequest));
	}

	@Transactional(readOnly = true)
	public List<LeaveResponse> getMyLeaves() {
		Employee employee = getCurrentEmployee();
		return leaveRepository.findByEmployeeIdOrderByCreatedAtDesc(employee.getId())
				.stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<LeaveResponse> getPendingLeaves() {
		assertAdminOrHr();
		return leaveRepository.findByStatusOrderByCreatedAtDesc(LeaveStatus.PENDING)
				.stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional
	public LeaveResponse approveLeave(Long leaveId, LeaveReviewRequest request) {
		return reviewLeave(leaveId, LeaveStatus.APPROVED, request);
	}

	@Transactional
	public LeaveResponse rejectLeave(Long leaveId, LeaveReviewRequest request) {
		return reviewLeave(leaveId, LeaveStatus.REJECTED, request);
	}

	private LeaveResponse reviewLeave(Long leaveId, LeaveStatus status, LeaveReviewRequest request) {
		assertAdminOrHr();
		LeaveRequest leaveRequest = findLeave(leaveId);

		if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
			throw new BadRequestException("Leave request is already " + leaveRequest.getStatus());
		}

		leaveRequest.setStatus(status);
		leaveRequest.setReviewComment(request.reviewComment());
		leaveRequest.setReviewedAt(LocalDateTime.now());

		return toResponse(leaveRequest);
	}

	private LeaveRequest findLeave(Long leaveId) {
		return leaveRepository.findById(leaveId)
				.orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + leaveId));
	}

	private Employee getCurrentEmployee() {
		User user = securityUtils.getCurrentUser();
		if (user.getEmployee() == null) {
			throw new BadRequestException("Employee profile is not linked to this user");
		}
		return user.getEmployee();
	}

	private void assertAdminOrHr() {
		User user = securityUtils.getCurrentUser();
		if (user.getRole() != Role.ADMIN && user.getRole() != Role.HR) {
			throw new ForbiddenException("Only ADMIN or HR can review leave requests");
		}
	}

	private LeaveResponse toResponse(LeaveRequest leaveRequest) {
		Employee employee = leaveRequest.getEmployee();
		return new LeaveResponse(
				leaveRequest.getId(),
				employee.getId(),
				employee.getFirstName() + " " + employee.getLastName(),
				leaveRequest.getLeaveType(),
				leaveRequest.getStartDate(),
				leaveRequest.getEndDate(),
				leaveRequest.getReason(),
				leaveRequest.getStatus(),
				leaveRequest.getReviewComment(),
				leaveRequest.getReviewedAt(),
				leaveRequest.getCreatedAt()
		);
	}
}
