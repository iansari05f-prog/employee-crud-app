package com.ems.crud.department;

import com.ems.crud.department.dto.DepartmentRequest;
import com.ems.crud.department.dto.DepartmentResponse;
import com.ems.crud.employee.Employee;
import com.ems.crud.employee.EmployeeRepository;
import com.ems.crud.employee.dto.EmployeeResponse;
import com.ems.crud.employee.EmployeeMapper;
import com.ems.crud.exception.DuplicateResourceException;
import com.ems.crud.exception.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepartmentService {

	private final DepartmentRepository departmentRepository;
	private final EmployeeRepository employeeRepository;
	private final EmployeeMapper employeeMapper;

	public DepartmentService(
			DepartmentRepository departmentRepository,
			EmployeeRepository employeeRepository,
			EmployeeMapper employeeMapper
	) {
		this.departmentRepository = departmentRepository;
		this.employeeRepository = employeeRepository;
		this.employeeMapper = employeeMapper;
	}

	@Transactional(readOnly = true)
	public List<DepartmentResponse> getAllDepartments() {
		return departmentRepository.findAll().stream().map(this::toResponse).toList();
	}

	@Transactional(readOnly = true)
	public DepartmentResponse getDepartmentById(Long id) {
		return toResponse(findDepartment(id));
	}

	@Transactional
	public DepartmentResponse createDepartment(DepartmentRequest request) {
		String name = request.name().trim();
		if (departmentRepository.existsByNameIgnoreCase(name)) {
			throw new DuplicateResourceException("Department already exists with name: " + name);
		}

		Department department = new Department(name, normalizeNullable(request.description()));
		return toResponse(departmentRepository.save(department));
	}

	@Transactional
	public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
		Department department = findDepartment(id);
		String name = request.name().trim();

		if (departmentRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
			throw new DuplicateResourceException("Department already exists with name: " + name);
		}

		department.setName(name);
		department.setDescription(normalizeNullable(request.description()));
		return toResponse(department);
	}

	@Transactional
	public void deleteDepartment(Long id) {
		findDepartment(id);
		if (employeeRepository.countByDepartmentId(id) > 0) {
			throw new DuplicateResourceException("Cannot delete department with assigned employees");
		}
		departmentRepository.deleteById(id);
	}

	@Transactional(readOnly = true)
	public List<EmployeeResponse> getEmployeesByDepartment(Long departmentId) {
		Department department = findDepartment(departmentId);
		return employeeRepository.findByDepartmentId(department.getId())
				.stream()
				.map(employeeMapper::toResponse)
				.toList();
	}

	@Transactional
	public EmployeeResponse assignEmployeeToDepartment(Long departmentId, Long employeeId) {
		Department department = findDepartment(departmentId);
		Employee employee = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
		employee.setDepartment(department);
		return employeeMapper.toResponse(employee);
	}

	private Department findDepartment(Long id) {
		return departmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
	}

	private DepartmentResponse toResponse(Department department) {
		return new DepartmentResponse(
				department.getId(),
				department.getName(),
				department.getDescription(),
				employeeRepository.countByDepartmentId(department.getId()),
				department.getCreatedAt(),
				department.getUpdatedAt()
		);
	}

	private String normalizeNullable(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.trim();
	}
}
