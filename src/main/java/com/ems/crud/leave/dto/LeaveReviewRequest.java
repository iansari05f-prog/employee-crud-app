package com.ems.crud.leave.dto;

import jakarta.validation.constraints.Size;

public record LeaveReviewRequest(
		@Size(max = 500) String reviewComment
) {
}
