package com.ems.crud.payroll.api;

import com.ems.crud.payroll.dto.PayrollGenerateRequest;
import com.ems.crud.payroll.dto.PayrollResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Payroll", description = "Generate payroll, view records, and download payslip PDFs")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/payrolls")
public interface PayrollApi {

	@Operation(summary = "Generate payroll", description = "Creates a payroll record for an employee and pay period. Admin or HR only.")
	@PostMapping("/generate")
	@PreAuthorize("hasAnyRole('ADMIN','HR')")
	ResponseEntity<PayrollResponse> generatePayroll(@Valid @RequestBody PayrollGenerateRequest request);

	@Operation(summary = "Get payroll by ID", description = "Returns a single payroll record by primary key.")
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
	ResponseEntity<PayrollResponse> getPayrollById(
			@Parameter(description = "Payroll record ID") @PathVariable Long id
	);

	@Operation(summary = "Payroll history by employee", description = "Returns all payroll records for the given employee.")
	@GetMapping("/employee/{employeeId}")
	@PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
	ResponseEntity<List<PayrollResponse>> getPayrollsByEmployee(
			@Parameter(description = "Employee ID") @PathVariable Long employeeId
	);

	@Operation(summary = "Download payslip PDF", description = "Generates and returns a payslip PDF file for the payroll record.")
	@GetMapping("/{id}/payslip")
	@PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
	ResponseEntity<byte[]> downloadPayslip(
			@Parameter(description = "Payroll record ID") @PathVariable Long id
	);
}
