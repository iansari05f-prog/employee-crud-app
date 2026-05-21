package com.ems.crud.payroll;

import com.ems.crud.payroll.dto.PayrollGenerateRequest;
import com.ems.crud.payroll.dto.PayrollResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payrolls")
@Tag(name = "Payroll", description = "Payroll generation and payslip download")
@SecurityRequirement(name = "bearerAuth")
public class PayrollController {

	private final PayrollService payrollService;

	public PayrollController(PayrollService payrollService) {
		this.payrollService = payrollService;
	}

	@PostMapping("/generate")
	@PreAuthorize("hasAnyRole('ADMIN','HR')")
	public ResponseEntity<PayrollResponse> generatePayroll(@Valid @RequestBody PayrollGenerateRequest request) {
		return ResponseEntity.ok(payrollService.generatePayroll(request));
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
	public ResponseEntity<PayrollResponse> getPayrollById(@PathVariable Long id) {
		return ResponseEntity.ok(payrollService.getPayrollById(id));
	}

	@GetMapping("/employee/{employeeId}")
	@PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
	public ResponseEntity<List<PayrollResponse>> getPayrollsByEmployee(@PathVariable Long employeeId) {
		return ResponseEntity.ok(payrollService.getPayrollsByEmployee(employeeId));
	}

	@GetMapping("/{id}/payslip")
	@PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
	public ResponseEntity<byte[]> downloadPayslip(@PathVariable Long id) {
		byte[] pdf = payrollService.generatePayslipPdf(id);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=payslip-" + id + ".pdf")
				.contentType(MediaType.APPLICATION_PDF)
				.body(pdf);
	}
}
