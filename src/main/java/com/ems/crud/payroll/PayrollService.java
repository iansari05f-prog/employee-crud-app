package com.ems.crud.payroll;

import com.ems.crud.employee.Employee;
import com.ems.crud.employee.EmployeeRepository;
import com.ems.crud.exception.DuplicateResourceException;
import com.ems.crud.exception.ForbiddenException;
import com.ems.crud.exception.ResourceNotFoundException;
import com.ems.crud.payroll.dto.PayrollGenerateRequest;
import com.ems.crud.payroll.dto.PayrollResponse;
import com.ems.crud.security.SecurityUtils;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PayrollService {

	private final PayrollRepository payrollRepository;
	private final EmployeeRepository employeeRepository;
	private final PayslipPdfService payslipPdfService;
	private final SecurityUtils securityUtils;

	public PayrollService(
			PayrollRepository payrollRepository,
			EmployeeRepository employeeRepository,
			PayslipPdfService payslipPdfService,
			SecurityUtils securityUtils
	) {
		this.payrollRepository = payrollRepository;
		this.employeeRepository = employeeRepository;
		this.payslipPdfService = payslipPdfService;
		this.securityUtils = securityUtils;
	}

	@Transactional
	public PayrollResponse generatePayroll(PayrollGenerateRequest request) {
		assertAdminOrHr();
		Employee employee = employeeRepository.findById(request.employeeId())
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + request.employeeId()));

		if (payrollRepository.existsByEmployeeIdAndPayPeriodMonthAndPayPeriodYear(
				employee.getId(), request.payPeriodMonth(), request.payPeriodYear())) {
			throw new DuplicateResourceException("Payroll already generated for this period");
		}

		BigDecimal netSalary = employee.getSalary()
				.add(request.bonus())
				.subtract(request.deductions());

		Payroll payroll = new Payroll(
				employee,
				employee.getSalary(),
				request.bonus(),
				request.deductions(),
				netSalary,
				request.payPeriodMonth(),
				request.payPeriodYear()
		);

		return toResponse(payrollRepository.save(payroll));
	}

	@Transactional(readOnly = true)
	public PayrollResponse getPayrollById(Long id) {
		Payroll payroll = findPayroll(id);
		assertCanViewPayroll(payroll);
		return toResponse(payroll);
	}

	@Transactional(readOnly = true)
	public List<PayrollResponse> getPayrollsByEmployee(Long employeeId) {
		if (!employeeRepository.existsById(employeeId)) {
			throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
		}

		if (!securityUtils.isAdminOrHr()) {
			var user = securityUtils.getCurrentUser();
			if (user.getEmployee() == null || !user.getEmployee().getId().equals(employeeId)) {
				throw new ForbiddenException("You can only view your own payroll records");
			}
		}

		return payrollRepository.findByEmployeeIdOrderByPayPeriodYearDescPayPeriodMonthDesc(employeeId)
				.stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public byte[] generatePayslipPdf(Long payrollId) {
		Payroll payroll = findPayroll(payrollId);
		assertCanViewPayroll(payroll);
		return payslipPdfService.generatePayslip(payroll);
	}

	private Payroll findPayroll(Long id) {
		return payrollRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Payroll not found with id: " + id));
	}

	private void assertCanViewPayroll(Payroll payroll) {
		if (securityUtils.isAdminOrHr()) {
			return;
		}
		var user = securityUtils.getCurrentUser();
		if (user.getEmployee() == null || !user.getEmployee().getId().equals(payroll.getEmployee().getId())) {
			throw new ForbiddenException("You can only view your own payroll");
		}
	}

	private void assertAdminOrHr() {
		if (!securityUtils.isAdminOrHr()) {
			throw new ForbiddenException("Only ADMIN or HR can generate payroll");
		}
	}

	private PayrollResponse toResponse(Payroll payroll) {
		Employee employee = payroll.getEmployee();
		return new PayrollResponse(
				payroll.getId(),
				employee.getId(),
				employee.getFirstName() + " " + employee.getLastName(),
				payroll.getBaseSalary(),
				payroll.getBonus(),
				payroll.getDeductions(),
				payroll.getNetSalary(),
				payroll.getPayPeriodMonth(),
				payroll.getPayPeriodYear(),
				payroll.getGeneratedAt()
		);
	}
}
