package com.ems.crud.document;

import com.ems.crud.common.DocumentType;
import com.ems.crud.common.Role;
import com.ems.crud.document.dto.DocumentResponse;
import com.ems.crud.employee.Employee;
import com.ems.crud.employee.EmployeeRepository;
import com.ems.crud.exception.ForbiddenException;
import com.ems.crud.exception.ResourceNotFoundException;
import com.ems.crud.security.SecurityUtils;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentService {

	private final EmployeeDocumentRepository documentRepository;
	private final EmployeeRepository employeeRepository;
	private final FileStorageService fileStorageService;
	private final SecurityUtils securityUtils;

	public DocumentService(
			EmployeeDocumentRepository documentRepository,
			EmployeeRepository employeeRepository,
			FileStorageService fileStorageService,
			SecurityUtils securityUtils
	) {
		this.documentRepository = documentRepository;
		this.employeeRepository = employeeRepository;
		this.fileStorageService = fileStorageService;
		this.securityUtils = securityUtils;
	}

	@Transactional
	public DocumentResponse uploadDocument(Long employeeId, DocumentType documentType, MultipartFile file) {
		assertCanManageEmployeeDocuments(employeeId);
		Employee employee = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

		String storedPath = fileStorageService.storeFile(file, "employees/" + employeeId + "/" + documentType.name().toLowerCase());
		EmployeeDocument document = new EmployeeDocument(
				employee,
				documentType,
				file.getOriginalFilename(),
				storedPath
		);

		if (documentType == DocumentType.PHOTO) {
			employee.setPhotoPath(storedPath);
		} else if (documentType == DocumentType.RESUME) {
			employee.setResumePath(storedPath);
		}

		return toResponse(documentRepository.save(document));
	}

	@Transactional(readOnly = true)
	public List<DocumentResponse> getEmployeeDocuments(Long employeeId) {
		assertCanManageEmployeeDocuments(employeeId);
		return documentRepository.findByEmployeeIdOrderByUploadedAtDesc(employeeId)
				.stream()
				.map(this::toResponse)
				.toList();
	}

	private void assertCanManageEmployeeDocuments(Long employeeId) {
		var user = securityUtils.getCurrentUser();
		if (user.getRole() == Role.ADMIN || user.getRole() == Role.HR) {
			return;
		}
		if (user.getEmployee() == null || !user.getEmployee().getId().equals(employeeId)) {
			throw new ForbiddenException("You can only manage your own documents");
		}
	}

	private DocumentResponse toResponse(EmployeeDocument document) {
		return new DocumentResponse(
				document.getId(),
				document.getEmployee().getId(),
				document.getDocumentType(),
				document.getOriginalFileName(),
				document.getStoredPath(),
				document.getUploadedAt()
		);
	}
}
