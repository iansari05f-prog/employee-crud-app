package com.ems.crud.employee;

import com.ems.crud.employee.dto.EmployeeResponse;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

	public EmployeeResponse toResponse(Employee employee) {
		return new EmployeeResponse(
				employee.getId(),
				employee.getFirstName(),
				employee.getLastName(),
				employee.getEmail(),
				employee.getPhoneNumber(),
				employee.getJobTitle(),
				employee.getDepartment().getId(),
				employee.getDepartment().getName(),
				employee.getSalary(),
				employee.getExperienceYears(),
				employee.getPhotoPath(),
				employee.getResumePath(),
				employee.getCreatedAt(),
				employee.getUpdatedAt()
		);
	}
}
