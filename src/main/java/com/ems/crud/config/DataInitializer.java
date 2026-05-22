package com.ems.crud.config;

import com.ems.crud.auth.User;
import com.ems.crud.auth.UserRepository;
import com.ems.crud.common.Role;
import com.ems.crud.department.Department;
import com.ems.crud.department.DepartmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

	@Bean
	CommandLineRunner seedDefaultAdmin(
			UserRepository userRepository,
			DepartmentRepository departmentRepository,
			PasswordEncoder passwordEncoder
	) {
		return args -> {
			if (userRepository.count() == 0) {
				User admin = new User("admin@ems.com", passwordEncoder.encode("admin123"), Role.ADMIN);
				userRepository.save(admin);
			}

			if (departmentRepository.count() == 0) {
				departmentRepository.save(new Department("IT", "Information Technology"));
				departmentRepository.save(new Department("HR", "Human Resources"));
				departmentRepository.save(new Department("Finance", "Finance Department"));
				departmentRepository.save(new Department("DevOps", "DevOps and Infrastructure"));
			}
		};
	}
}
