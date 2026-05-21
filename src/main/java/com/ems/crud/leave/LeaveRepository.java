package com.ems.crud.leave;

import com.ems.crud.common.LeaveStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveRepository extends JpaRepository<LeaveRequest, Long> {

	List<LeaveRequest> findByEmployeeIdOrderByCreatedAtDesc(Long employeeId);

	List<LeaveRequest> findByStatusOrderByCreatedAtDesc(LeaveStatus status);
}
