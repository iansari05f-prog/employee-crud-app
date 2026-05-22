package com.ems.crud.payroll;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Service;

@Service
public class PayslipPdfService {

	public byte[] generatePayslip(Payroll payroll) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			Document document = new Document();
			PdfWriter.getInstance(document, outputStream);
			document.open();

			Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
			Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

			document.add(new Paragraph("Employee Management System - Payslip", titleFont));
			document.add(new Paragraph(" "));
			document.add(new Paragraph("Employee: " + payroll.getEmployee().getFirstName() + " "
					+ payroll.getEmployee().getLastName(), bodyFont));
			document.add(new Paragraph("Email: " + payroll.getEmployee().getEmail(), bodyFont));
			document.add(new Paragraph("Period: " + payroll.getPayPeriodMonth() + "/" + payroll.getPayPeriodYear(), bodyFont));
			document.add(new Paragraph(" "));
			document.add(new Paragraph("Base Salary: " + payroll.getBaseSalary(), bodyFont));
			document.add(new Paragraph("Bonus: " + payroll.getBonus(), bodyFont));
			document.add(new Paragraph("Deductions: " + payroll.getDeductions(), bodyFont));
			document.add(new Paragraph("Net Salary: " + payroll.getNetSalary(), bodyFont));
			document.add(new Paragraph(" "));
			document.add(new Paragraph("Generated At: " + payroll.getGeneratedAt(), bodyFont));

			document.close();
			return outputStream.toByteArray();
		} catch (DocumentException exception) {
			throw new IllegalStateException("Failed to generate payslip PDF", exception);
		} finally {
			try {
				outputStream.close();
			} catch (java.io.IOException ignored) {
				// no-op
			}
		}
	}
}
