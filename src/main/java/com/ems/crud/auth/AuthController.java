package com.ems.crud.auth;

import com.ems.crud.auth.api.AuthApi;
import com.ems.crud.auth.dto.AuthResponse;
import com.ems.crud.auth.dto.ChangePasswordRequest;
import com.ems.crud.auth.dto.ForgotPasswordRequest;
import com.ems.crud.auth.dto.ForgotPasswordResponse;
import com.ems.crud.auth.dto.LoginRequest;
import com.ems.crud.auth.dto.RegisterRequest;
import com.ems.crud.auth.dto.ResetPasswordRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController implements AuthApi {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@Override
	public ResponseEntity<AuthResponse> register(RegisterRequest request) {
		return ResponseEntity.ok(authService.register(request));
	}

	@Override
	public ResponseEntity<AuthResponse> login(LoginRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}

	@Override
	public ResponseEntity<Void> changePassword(ChangePasswordRequest request) {
		authService.changePassword(request);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<ForgotPasswordResponse> forgotPassword(ForgotPasswordRequest request) {
		return ResponseEntity.ok(authService.forgotPassword(request));
	}

	@Override
	public ResponseEntity<Void> resetPassword(ResetPasswordRequest request) {
		authService.resetPassword(request);
		return ResponseEntity.noContent().build();
	}
}
