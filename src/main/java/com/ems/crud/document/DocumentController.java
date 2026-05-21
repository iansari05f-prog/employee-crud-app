package com.ems.crud.document;

import com.ems.crud.common.DocumentType;
import com.ems.crud.document.dto.DocumentResponse;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/employees/{employeeId}/documents")
@Tag(name = "Documents", description = "Upload photo, resume, and other employee documents")
@SecurityRequirement(name = "bearerAuth")
public class DocumentController {

	private final DocumentService documentService;

	public DocumentController(DocumentService documentService) {
		this.documentService = documentService;
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
	public ResponseEntity<DocumentResponse> uploadDocument(
			@PathVariable Long employeeId,
			@RequestParam DocumentType documentType,
			@RequestParam("file") MultipartFile file
	) {
		return ResponseEntity.ok(documentService.uploadDocument(employeeId, documentType, file));
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
	public ResponseEntity<List<DocumentResponse>> getEmployeeDocuments(@PathVariable Long employeeId) {
		return ResponseEntity.ok(documentService.getEmployeeDocuments(employeeId));
	}
}
