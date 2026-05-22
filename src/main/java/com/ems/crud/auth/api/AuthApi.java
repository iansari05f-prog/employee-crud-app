package com.ems.crud.auth.api;

import com.ems.crud.auth.dto.AuthResponse;
import com.ems.crud.auth.dto.ChangePasswordRequest;
import com.ems.crud.auth.dto.ForgotPasswordRequest;
import com.ems.crud.auth.dto.ForgotPasswordResponse;
import com.ems.crud.auth.dto.LoginRequest;
import com.ems.crud.auth.dto.RegisterRequest;
import com.ems.crud.auth.dto.ResetPasswordRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Authentication", description = "Register, login, password management, and JWT tokens")
@RequestMapping("/api/v1/auth")
public interface AuthApi {

	@Operation(
			summary = "Register a new user",
			description = "Creates an EMPLOYEE user linked to an existing employee profile. Admin-created employees already receive accounts."
	)
	@PostMapping("/register")
	ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request);

	@Operation(summary = "Login", description = "Authenticates with email and password and returns a JWT for protected endpoints.")
	@PostMapping("/login")
	ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request);

	@Operation(
			summary = "Change password",
			description = "Updates the logged-in user's password. Skips current password when mustChangePassword is true."
	)
	@SecurityRequirement(name = "bearerAuth")
	@PostMapping("/change-password")
	ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request);

	@Operation(
			summary = "Forgot password",
			description = "Requests a password reset token for the given email. In local profile, resetToken may be returned for testing."
	)
	@PostMapping("/forgot-password")
	ResponseEntity<ForgotPasswordResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request);

	@Operation(summary = "Reset password", description = "Sets a new password using a valid reset token from forgot-password.")
	@PostMapping("/reset-password")
	ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request);
}
