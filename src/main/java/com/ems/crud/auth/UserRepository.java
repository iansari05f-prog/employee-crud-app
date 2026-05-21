package com.ems.crud.auth;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmailIgnoreCase(String email);

	@Query("SELECT u FROM User u LEFT JOIN FETCH u.employee WHERE LOWER(u.email) = LOWER(:email)")
	Optional<User> findByEmailIgnoreCaseWithEmployee(@Param("email") String email);

	boolean existsByEmailIgnoreCase(String email);
}
