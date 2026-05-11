package com.ems.crud.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiError> handleResourceNotFoundException(
			ResourceNotFoundException exception,
			HttpServletRequest request
	) {
		return buildErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request, null);
	}

	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<ApiError> handleDuplicateResourceException(
			DuplicateResourceException exception,
			HttpServletRequest request
	) {
		return buildErrorResponse(HttpStatus.CONFLICT, exception.getMessage(), request, null);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleMethodArgumentNotValidException(
			MethodArgumentNotValidException exception,
			HttpServletRequest request
	) {
		Map<String, String> validationErrors = new LinkedHashMap<>();
		exception.getBindingResult().getFieldErrors()
				.forEach(error -> validationErrors.put(error.getField(), error.getDefaultMessage()));

		return buildErrorResponse(HttpStatus.BAD_REQUEST, "Request validation failed", request, validationErrors);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiError> handleDataIntegrityViolationException(
			DataIntegrityViolationException exception,
			HttpServletRequest request
	) {
		return buildErrorResponse(HttpStatus.CONFLICT, "Request violates a database constraint", request, null);
	}

	private ResponseEntity<ApiError> buildErrorResponse(
			HttpStatus status,
			String message,
			HttpServletRequest request,
			Map<String, String> validationErrors
	) {
		ApiError apiError = new ApiError(
				LocalDateTime.now(),
				status.value(),
				status.getReasonPhrase(),
				message,
				request.getRequestURI(),
				validationErrors
		);

		return ResponseEntity.status(status).body(apiError);
	}
}
