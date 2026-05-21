package com.ems.crud.auth;

import com.ems.crud.auth.dto.AuthResponse;
import com.ems.crud.auth.dto.LoginRequest;
import com.ems.crud.auth.dto.RegisterRequest;
import com.ems.crud.common.Role;
import com.ems.crud.employee.Employee;
import com.ems.crud.employee.EmployeeRepository;
import com.ems.crud.exception.BadRequestException;
import com.ems.crud.exception.DuplicateResourceException;
import com.ems.crud.exception.ResourceNotFoundException;
import com.ems.crud.security.CustomUserDetailsService;
import com.ems.crud.security.JwtService;
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

	public AuthService(
			UserRepository userRepository,
			EmployeeRepository employeeRepository,
			PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager,
			JwtService jwtService,
			CustomUserDetailsService userDetailsService
	) {
		this.userRepository = userRepository;
		this.employeeRepository = employeeRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

	@Transactional
	public AuthResponse register(RegisterRequest request) {
		String email = normalizeEmail(request.email());

		if (userRepository.existsByEmailIgnoreCase(email)) {
			throw new DuplicateResourceException("User already exists with email: " + email);
		}

		User user = new User(email, passwordEncoder.encode(request.password()), request.role());

		if (request.employeeId() != null) {
			Employee employee = employeeRepository.findById(request.employeeId())
					.orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + request.employeeId()));
			user.setEmployee(employee);
		}

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

	private String generateToken(User user) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
		return jwtService.generateToken(userDetails, Map.of(
				"role", user.getRole().name(),
				"userId", user.getId(),
				"employeeId", user.getEmployee() != null ? user.getEmployee().getId() : null
		));
	}

	private AuthResponse buildAuthResponse(User user, String token) {
		return new AuthResponse(
				token,
				"Bearer",
				user.getId(),
				user.getEmail(),
				user.getRole(),
				user.getEmployee() != null ? user.getEmployee().getId() : null
		);
	}

	private String normalizeEmail(String email) {
		return email.trim().toLowerCase(Locale.ROOT);
	}
}
