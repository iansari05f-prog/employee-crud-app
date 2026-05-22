package com.ems.crud.payroll;

import com.ems.crud.payroll.api.PayrollApi;
import com.ems.crud.payroll.dto.PayrollGenerateRequest;
import com.ems.crud.payroll.dto.PayrollResponse;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PayrollController implements PayrollApi {

	private final PayrollService payrollService;

	public PayrollController(PayrollService payrollService) {
		this.payrollService = payrollService;
	}

	@Override
	public ResponseEntity<PayrollResponse> generatePayroll(PayrollGenerateRequest request) {
		return ResponseEntity.ok(payrollService.generatePayroll(request));
	}

	@Override
	public ResponseEntity<PayrollResponse> getPayrollById(Long id) {
		return ResponseEntity.ok(payrollService.getPayrollById(id));
	}

	@Override
	public ResponseEntity<List<PayrollResponse>> getPayrollsByEmployee(Long employeeId) {
		return ResponseEntity.ok(payrollService.getPayrollsByEmployee(employeeId));
	}

	@Override
	public ResponseEntity<byte[]> downloadPayslip(Long id) {
		byte[] pdf = payrollService.generatePayslipPdf(id);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=payslip-" + id + ".pdf")
				.contentType(MediaType.APPLICATION_PDF)
				.body(pdf);
	}
}
