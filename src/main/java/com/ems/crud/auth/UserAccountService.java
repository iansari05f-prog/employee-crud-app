package com.ems.crud.auth;

import com.ems.crud.common.Role;
import com.ems.crud.employee.Employee;
import com.ems.crud.exception.DuplicateResourceException;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserAccountService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserAccountService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public User createEmployeeAccount(Employee employee, String rawPassword, boolean requirePasswordChange) {
		String email = normalizeEmail(employee.getEmail());
		if (userRepository.existsByEmailIgnoreCase(email)) {
			throw new DuplicateResourceException("User already exists with email: " + email);
		}
		User user = new User(email, passwordEncoder.encode(rawPassword), Role.EMPLOYEE);
		user.setEmployee(employee);
		user.setMustChangePassword(requirePasswordChange);
		return userRepository.save(user);
	}

	public void syncEmployeeEmail(Employee employee, String newEmail) {
		userRepository.findByEmployeeId(employee.getId()).ifPresent(user -> {
			String normalizedEmail = normalizeEmail(newEmail);
			if (!normalizedEmail.equalsIgnoreCase(user.getEmail())
					&& userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
				throw new DuplicateResourceException("User already exists with email: " + normalizedEmail);
			}
			user.setEmail(normalizedEmail);
			userRepository.save(user);
		});
	}

	public void deleteAccountForEmployee(Long employeeId) {
		userRepository.deleteByEmployeeId(employeeId);
	}

	private String normalizeEmail(String email) {
		return email.trim().toLowerCase(Locale.ROOT);
	}
}
