package com.ems.crud.document.dto;

import com.ems.crud.common.DocumentType;
import java.time.LocalDateTime;

public record DocumentResponse(
		Long id,
		Long employeeId,
		DocumentType documentType,
		String originalFileName,
		String storedPath,
		LocalDateTime uploadedAt
) {
}
