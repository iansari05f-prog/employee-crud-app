package com.ems.crud.document;

import com.ems.crud.common.DocumentType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument, Long> {

	List<EmployeeDocument> findByEmployeeIdOrderByUploadedAtDesc(Long employeeId);

	List<EmployeeDocument> findByEmployeeIdAndDocumentTypeOrderByUploadedAtDesc(Long employeeId, DocumentType documentType);
}
