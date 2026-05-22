package com.ems.crud.employee;

import java.math.BigDecimal;
import org.springframework.data.jpa.domain.Specification;

public final class EmployeeSpecification {

	private EmployeeSpecification() {
	}

	public static Specification<Employee> withFilters(
			String name,
			Long departmentId,
			BigDecimal minSalary,
			BigDecimal maxSalary,
			Integer minExperience
	) {
		return Specification.where(nameContains(name))
				.and(departmentEquals(departmentId))
				.and(salaryGreaterOrEqual(minSalary))
				.and(salaryLessOrEqual(maxSalary))
				.and(experienceGreaterOrEqual(minExperience));
	}

	private static Specification<Employee> nameContains(String name) {
		return (root, query, cb) -> {
			if (name == null || name.isBlank()) {
				return cb.conjunction();
			}
			String pattern = "%" + name.trim().toLowerCase() + "%";
			return cb.or(
					cb.like(cb.lower(root.get("firstName")), pattern),
					cb.like(cb.lower(root.get("lastName")), pattern)
			);
		};
	}

	private static Specification<Employee> departmentEquals(Long departmentId) {
		return (root, query, cb) -> {
			if (departmentId == null) {
				return cb.conjunction();
			}
			return cb.equal(root.get("department").get("id"), departmentId);
		};
	}

	private static Specification<Employee> salaryGreaterOrEqual(BigDecimal minSalary) {
		return (root, query, cb) -> {
			if (minSalary == null) {
				return cb.conjunction();
			}
			return cb.greaterThanOrEqualTo(root.get("salary"), minSalary);
		};
	}

	private static Specification<Employee> salaryLessOrEqual(BigDecimal maxSalary) {
		return (root, query, cb) -> {
			if (maxSalary == null) {
				return cb.conjunction();
			}
			return cb.lessThanOrEqualTo(root.get("salary"), maxSalary);
		};
	}

	private static Specification<Employee> experienceGreaterOrEqual(Integer minExperience) {
		return (root, query, cb) -> {
			if (minExperience == null) {
				return cb.conjunction();
			}
			return cb.greaterThanOrEqualTo(root.get("experienceYears"), minExperience);
		};
	}
}
