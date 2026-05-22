package com.ems.crud.auth;

import com.ems.crud.auth.dto.ForgotPasswordResponse;
import com.ems.crud.auth.dto.ResetPasswordRequest;
import com.ems.crud.config.AppProperties;
import com.ems.crud.exception.BadRequestException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordResetService {

	private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);
	private static final String RESET_MESSAGE =
			"If an account with that email exists, password reset instructions have been sent.";

	private final UserRepository userRepository;
	private final PasswordResetTokenRepository tokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final AppProperties appProperties;
	private final SecureRandom secureRandom = new SecureRandom();

	public PasswordResetService(
			UserRepository userRepository,
			PasswordResetTokenRepository tokenRepository,
			PasswordEncoder passwordEncoder,
			AppProperties appProperties
	) {
		this.userRepository = userRepository;
		this.tokenRepository = tokenRepository;
		this.passwordEncoder = passwordEncoder;
		this.appProperties = appProperties;
	}

	@Transactional
	public ForgotPasswordResponse requestReset(String email) {
		String normalizedEmail = normalizeEmail(email);
		String resetToken = userRepository.findByEmailIgnoreCase(normalizedEmail)
				.map(this::issueResetToken)
				.orElse(null);

		if (!appProperties.passwordReset().exposeTokenInResponse()) {
			resetToken = null;
		}

		return new ForgotPasswordResponse(RESET_MESSAGE, resetToken);
	}

	@Transactional
	public void resetPassword(ResetPasswordRequest request) {
		String tokenHash = hashToken(request.token());
		PasswordResetToken resetToken = tokenRepository.findByTokenHashAndUsedFalse(tokenHash)
				.orElseThrow(() -> new BadRequestException("Invalid or expired password reset token"));

		if (resetToken.isExpired()) {
			throw new BadRequestException("Invalid or expired password reset token");
		}

		User user = resetToken.getUser();
		user.setPassword(passwordEncoder.encode(request.newPassword()));
		user.setMustChangePassword(false);
		resetToken.setUsed(true);
		tokenRepository.invalidateActiveTokensForUser(user.getId());
	}

	private String issueResetToken(User user) {
		tokenRepository.invalidateActiveTokensForUser(user.getId());

		String rawToken = generateRawToken();
		String tokenHash = hashToken(rawToken);
		LocalDateTime expiresAt = LocalDateTime.now()
				.plusMinutes(appProperties.passwordReset().expirationMinutes());

		tokenRepository.save(new PasswordResetToken(user, tokenHash, expiresAt));
		log.info("Password reset token issued for {}", user.getEmail());
		return rawToken;
	}

	private String generateRawToken() {
		byte[] bytes = new byte[32];
		secureRandom.nextBytes(bytes);
		return HexFormat.of().formatHex(bytes);
	}

	static String hashToken(String rawToken) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(hash);
		} catch (NoSuchAlgorithmException exception) {
			throw new IllegalStateException("SHA-256 not available", exception);
		}
	}

	private String normalizeEmail(String email) {
		return email.trim().toLowerCase(Locale.ROOT);
	}
}
