package com.ems.crud.document;

import com.ems.crud.common.DocumentType;
import com.ems.crud.employee.Employee;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employee_documents")
@Getter
@Setter
@NoArgsConstructor
public class EmployeeDocument {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "employee_id", nullable = false)
	private Employee employee;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private DocumentType documentType;

	@Column(nullable = false, length = 255)
	private String originalFileName;

	@Column(nullable = false, length = 500)
	private String storedPath;

	@Column(nullable = false, updatable = false)
	private LocalDateTime uploadedAt;

	public EmployeeDocument(Employee employee, DocumentType documentType, String originalFileName, String storedPath) {
		this.employee = employee;
		this.documentType = documentType;
		this.originalFileName = originalFileName;
		this.storedPath = storedPath;
	}

	@PrePersist
	void onCreate() {
		uploadedAt = LocalDateTime.now();
	}
}
