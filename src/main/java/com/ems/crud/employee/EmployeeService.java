package com.ems.crud.employee;

import com.ems.crud.auth.User;
import com.ems.crud.common.Role;
import com.ems.crud.department.Department;
import com.ems.crud.department.DepartmentRepository;
import com.ems.crud.employee.dto.EmployeeRequest;
import com.ems.crud.employee.dto.EmployeeResponse;
import com.ems.crud.exception.DuplicateResourceException;
import com.ems.crud.exception.ForbiddenException;
import com.ems.crud.exception.ResourceNotFoundException;
import com.ems.crud.security.SecurityUtils;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {

	private final EmployeeRepository employeeRepository;
	private final DepartmentRepository departmentRepository;
	private final EmployeeMapper employeeMapper;
	private final SecurityUtils securityUtils;

	public EmployeeService(
			EmployeeRepository employeeRepository,
			DepartmentRepository departmentRepository,
			EmployeeMapper employeeMapper,
			SecurityUtils securityUtils
	) {
		this.employeeRepository = employeeRepository;
		this.departmentRepository = departmentRepository;
		this.employeeMapper = employeeMapper;
		this.securityUtils = securityUtils;
	}

	@Transactional(readOnly = true)
	public List<EmployeeResponse> getAllEmployees() {
		assertAdminOrHr();
		return employeeRepository.findAll().stream().map(employeeMapper::toResponse).toList();
	}

	@Transactional(readOnly = true)
	public EmployeeResponse getEmployeeById(Long id) {
		Employee employee = findEmployeeById(id);
		assertCanViewEmployee(employee);
		return employeeMapper.toResponse(employee);
	}

	@Transactional(readOnly = true)
	public EmployeeResponse getMyProfile() {
		User user = securityUtils.getCurrentUser();
		if (user.getEmployee() == null) {
			throw new ResourceNotFoundException("No employee profile linked to this user");
		}
		return employeeMapper.toResponse(user.getEmployee());
	}

	@Transactional(readOnly = true)
	public List<EmployeeResponse> searchEmployees(
			String name,
			Long departmentId,
			BigDecimal minSalary,
			BigDecimal maxSalary,
			Integer minExperience
	) {
		assertAdminOrHr();
		return employeeRepository.findAll(EmployeeSpecification.withFilters(
						name, departmentId, minSalary, maxSalary, minExperience))
				.stream()
				.map(employeeMapper::toResponse)
				.toList();
	}

	@Transactional
	public EmployeeResponse createEmployee(EmployeeRequest request) {
		assertAdminOrHr();
		String normalizedEmail = normalizeEmail(request.email());
		validateEmailIsAvailable(normalizedEmail);

		Department department = findDepartment(request.departmentId());
		Employee employee = new Employee(
				request.firstName().trim(),
				request.lastName().trim(),
				normalizedEmail,
				normalizeNullable(request.phoneNumber()),
				request.jobTitle().trim(),
				department,
				request.salary(),
				request.experienceYears()
		);

		return employeeMapper.toResponse(employeeRepository.save(employee));
	}

	@Transactional
	public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
		assertAdminOrHr();
		Employee employee = findEmployeeById(id);
		String normalizedEmail = normalizeEmail(request.email());
		validateEmailIsAvailableForEmployee(normalizedEmail, id);

		employee.setFirstName(request.firstName().trim());
		employee.setLastName(request.lastName().trim());
		employee.setEmail(normalizedEmail);
		employee.setPhoneNumber(normalizeNullable(request.phoneNumber()));
		employee.setJobTitle(request.jobTitle().trim());
		employee.setDepartment(findDepartment(request.departmentId()));
		employee.setSalary(request.salary());
		employee.setExperienceYears(request.experienceYears());

		return employeeMapper.toResponse(employee);
	}

	@Transactional
	public void deleteEmployee(Long id) {
		assertAdminOrHr();
		if (!employeeRepository.existsById(id)) {
			throw new ResourceNotFoundException("Employee not found with id: " + id);
		}
		employeeRepository.deleteById(id);
	}

	private Employee findEmployeeById(Long id) {
		return employeeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
	}

	private Department findDepartment(Long departmentId) {
		return departmentRepository.findById(departmentId)
				.orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));
	}

	private void validateEmailIsAvailable(String email) {
		if (employeeRepository.existsByEmailIgnoreCase(email)) {
			throw new DuplicateResourceException("Employee already exists with email: " + email);
		}
	}

	private void validateEmailIsAvailableForEmployee(String email, Long employeeId) {
		if (employeeRepository.existsByEmailIgnoreCaseAndIdNot(email, employeeId)) {
			throw new DuplicateResourceException("Employee already exists with email: " + email);
		}
	}

	private void assertAdminOrHr() {
		User user = securityUtils.getCurrentUser();
		if (user.getRole() != Role.ADMIN && user.getRole() != Role.HR) {
			throw new ForbiddenException("Only ADMIN or HR can perform this action");
		}
	}

	private void assertCanViewEmployee(Employee employee) {
		User user = securityUtils.getCurrentUser();
		if (user.getRole() == Role.ADMIN || user.getRole() == Role.HR) {
			return;
		}
		if (user.getEmployee() == null || !user.getEmployee().getId().equals(employee.getId())) {
			throw new ForbiddenException("You can only view your own employee profile");
		}
	}

	private String normalizeEmail(String email) {
		return email.trim().toLowerCase(Locale.ROOT);
	}

	private String normalizeNullable(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.trim();
	}
}
