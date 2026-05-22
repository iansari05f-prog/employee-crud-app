package com.ems.crud.document.api;

import com.ems.crud.common.DocumentType;
import com.ems.crud.document.dto.DocumentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Documents", description = "Upload and list employee documents (photo, resume, ID proof, etc.)")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/employees/{employeeId}/documents")
public interface DocumentApi {
    @Operation
	(summary = "Upload document", description = "Uploads a file for an employee. Supported types: PHOTO, RESUME, ID_PROOF, CONTRACT, OTHER.")
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
	ResponseEntity<DocumentResponse> uploadDocument(
			@Parameter(description = "Employee ID") @PathVariable Long employeeId,
			@Parameter(description = "Document category") @RequestParam DocumentType documentType,
			@Parameter(description = "File to upload") @RequestParam("file") MultipartFile file
	);

	@Operation(summary = "List employee documents", description = "Returns metadata for all documents stored for the employee.")
	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
	ResponseEntity<List<DocumentResponse>> getEmployeeDocuments(
			@Parameter(description = "Employee ID") @PathVariable Long employeeId
	);
}
