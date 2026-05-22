package com.ems.crud.document;

import com.ems.crud.common.DocumentType;
import com.ems.crud.document.api.DocumentApi;
import com.ems.crud.document.dto.DocumentResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class DocumentController implements DocumentApi {

	private final DocumentService documentService;

	public DocumentController(DocumentService documentService) {
		this.documentService = documentService;
	}

	@Override
	public ResponseEntity<DocumentResponse> uploadDocument(
			Long employeeId,
			DocumentType documentType,
			MultipartFile file
	) {
		return ResponseEntity.ok(documentService.uploadDocument(employeeId, documentType, file));
	}

	@Override
	public ResponseEntity<List<DocumentResponse>> getEmployeeDocuments(Long employeeId) {
		return ResponseEntity.ok(documentService.getEmployeeDocuments(employeeId));
	}
}
