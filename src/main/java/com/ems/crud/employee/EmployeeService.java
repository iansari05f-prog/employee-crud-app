package com.ems.crud.employee;

import com.ems.crud.exception.DuplicateResourceException;
import com.ems.crud.exception.ResourceNotFoundException;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {

	private final EmployeeRepository employeeRepository;

	public EmployeeService(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}

	@Transactional(readOnly = true)
	public List<EmployeeResponse> getAllEmployees() {
		return employeeRepository.findAll()
				.stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public EmployeeResponse getEmployeeById(Long id) {
		return toResponse(findEmployeeById(id));
	}

	@Transactional
	public EmployeeResponse createEmployee(EmployeeRequest request) {
		String normalizedEmail = normalizeEmail(request.email());
		validateEmailIsAvailable(normalizedEmail);

		Employee employee = new Employee(
				request.firstName().trim(),
				request.lastName().trim(),
				normalizedEmail,
				normalizeNullable(request.phoneNumber()),
				request.jobTitle().trim(),
				request.department().trim(),
				request.salary()
		);

		return toResponse(employeeRepository.save(employee));
	}

	@Transactional
	public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
		Employee employee = findEmployeeById(id);
		String normalizedEmail = normalizeEmail(request.email());
		validateEmailIsAvailableForEmployee(normalizedEmail, id);

		employee.setFirstName(request.firstName().trim());
		employee.setLastName(request.lastName().trim());
		employee.setEmail(normalizedEmail);
		employee.setPhoneNumber(normalizeNullable(request.phoneNumber()));
		employee.setJobTitle(request.jobTitle().trim());
		employee.setDepartment(request.department().trim());
		employee.setSalary(request.salary());

		return toResponse(employee);
	}

	@Transactional
	public void deleteEmployee(Long id) {
		if (!employeeRepository.existsById(id)) {
			throw new ResourceNotFoundException("Employee not found with id: " + id);
		}

		employeeRepository.deleteById(id);
	}

	private Employee findEmployeeById(Long id) {
		return employeeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
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

	private EmployeeResponse toResponse(Employee employee) {
		return new EmployeeResponse(
				employee.getId(),
				employee.getFirstName(),
				employee.getLastName(),
				employee.getEmail(),
				employee.getPhoneNumber(),
				employee.getJobTitle(),
				employee.getDepartment(),
				employee.getSalary(),
				employee.getCreatedAt(),
				employee.getUpdatedAt()
		);
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
