package com.ems.crud.payroll;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {

	List<Payroll> findByEmployeeIdOrderByPayPeriodYearDescPayPeriodMonthDesc(Long employeeId);

	boolean existsByEmployeeIdAndPayPeriodMonthAndPayPeriodYear(Long employeeId, Integer month, Integer year);

	Optional<Payroll> findByIdAndEmployeeId(Long id, Long employeeId);
}
