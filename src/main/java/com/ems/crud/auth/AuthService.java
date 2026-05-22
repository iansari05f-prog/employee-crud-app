package com.ems.crud.auth;

import com.ems.crud.auth.dto.AuthResponse;
import com.ems.crud.auth.dto.ChangePasswordRequest;
import com.ems.crud.auth.dto.ForgotPasswordRequest;
import com.ems.crud.auth.dto.ForgotPasswordResponse;
import com.ems.crud.auth.dto.LoginRequest;
import com.ems.crud.auth.dto.RegisterRequest;
import com.ems.crud.auth.dto.ResetPasswordRequest;
import com.ems.crud.common.Role;
import com.ems.crud.employee.Employee;
import com.ems.crud.employee.EmployeeRepository;
import com.ems.crud.exception.BadRequestException;
import com.ems.crud.exception.DuplicateResourceException;
import com.ems.crud.exception.ForbiddenException;
import com.ems.crud.exception.ResourceNotFoundException;
import com.ems.crud.security.CustomUserDetailsService;
import com.ems.crud.security.JwtService;
import com.ems.crud.security.SecurityUtils;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final EmployeeRepository employeeRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final CustomUserDetailsService userDetailsService;
	private final SecurityUtils securityUtils;
	private final PasswordResetService passwordResetService;

	public AuthService(
			UserRepository userRepository,
			EmployeeRepository employeeRepository,
			PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager,
			JwtService jwtService,
			CustomUserDetailsService userDetailsService,
			SecurityUtils securityUtils,
			PasswordResetService passwordResetService
	) {
		this.userRepository = userRepository;
		this.employeeRepository = employeeRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
		this.securityUtils = securityUtils;
		this.passwordResetService = passwordResetService;
	}

	@Transactional
	public AuthResponse register(RegisterRequest request) {
		if (request.role() != Role.EMPLOYEE) {
			throw new ForbiddenException("Public registration is only allowed for EMPLOYEE accounts");
		}
		if (request.employeeId() == null) {
			throw new BadRequestException("employeeId is required to link an employee profile");
		}

		String email = normalizeEmail(request.email());
		if (userRepository.existsByEmailIgnoreCase(email)) {
			throw new DuplicateResourceException("User already exists with email: " + email);
		}

		Employee employee = employeeRepository.findById(request.employeeId())
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + request.employeeId()));

		if (!email.equalsIgnoreCase(employee.getEmail())) {
			throw new BadRequestException("Registration email must match the employee profile email");
		}
		if (userRepository.existsByEmployeeId(employee.getId())) {
			throw new DuplicateResourceException("A login account already exists for this employee");
		}

		User user = new User(email, passwordEncoder.encode(request.password()), request.role());
		user.setEmployee(employee);

		User savedUser = userRepository.save(user);
		return buildAuthResponse(savedUser, generateToken(savedUser));
	}

	@Transactional(readOnly = true)
	public AuthResponse login(LoginRequest request) {
		String email = normalizeEmail(request.email());
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(email, request.password())
		);

		User user = userRepository.findByEmailIgnoreCase(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

		return buildAuthResponse(user, generateToken(user));
	}

	@Transactional
	public void changePassword(ChangePasswordRequest request) {
		User user = securityUtils.getCurrentUser();

		if (!user.isMustChangePassword()) {
			if (request.currentPassword() == null || request.currentPassword().isBlank()) {
				throw new BadRequestException("Current password is required");
			}
			if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
				throw new BadRequestException("Current password is incorrect");
			}
		}

		user.setPassword(passwordEncoder.encode(request.newPassword()));
		user.setMustChangePassword(false);
		userRepository.save(user);
	}

	@Transactional(readOnly = true)
	public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
		return passwordResetService.requestReset(request.email());
	}

	@Transactional
	public void resetPassword(ResetPasswordRequest request) {
		passwordResetService.resetPassword(request);
	}

	private String generateToken(User user) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", user.getRole().name());
		claims.put("userId", user.getId());
		if (user.getEmployee() != null) {
			claims.put("employeeId", user.getEmployee().getId());
		}
		return jwtService.generateToken(userDetails, claims);
	}

	private AuthResponse buildAuthResponse(User user, String token) {
		return new AuthResponse(
				token,
				"Bearer",
				user.getId(),
				user.getEmail(),
				user.getRole(),
				user.getEmployee() != null ? user.getEmployee().getId() : null,
				user.isMustChangePassword()
		);
	}

	private String normalizeEmail(String email) {
		return email.trim().toLowerCase(Locale.ROOT);
	}
}
