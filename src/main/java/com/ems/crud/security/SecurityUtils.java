package com.ems.crud.security;

import com.ems.crud.auth.User;
import com.ems.crud.auth.UserRepository;
import com.ems.crud.common.Role;
import com.ems.crud.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

	private final UserRepository userRepository;

	public SecurityUtils(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new UnauthorizedException("User is not authenticated");
		}

		return userRepository.findByEmailIgnoreCaseWithEmployee(authentication.getName())
				.orElseThrow(() -> new UnauthorizedException("Authenticated user not found"));
	}

	public boolean hasRole(Role role) {
		return getCurrentUser().getRole() == role;
	}

	public boolean isAdminOrHr() {
		Role role = getCurrentUser().getRole();
		return role == Role.ADMIN || role == Role.HR;
	}
}
