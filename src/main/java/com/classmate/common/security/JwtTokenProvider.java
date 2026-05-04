package com.classmate.common.security;

import com.classmate.auth.domain.User;
import com.classmate.common.exception.BusinessException;
import com.classmate.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

	private final SecretKey secretKey;
	private final long accessTokenExpirationMs;

	public JwtTokenProvider(
			@Value("${jwt.secret}") String secret,
			@Value("${jwt.access-token-expiration-ms}") long accessTokenExpirationMs
	) {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.accessTokenExpirationMs = accessTokenExpirationMs;
	}

	public String createAccessToken(User user) {
		Date now = new Date();
		Date expiresAt = new Date(now.getTime() + accessTokenExpirationMs);

		return Jwts.builder()
				.subject(user.getEmail())
				.claim("userId", user.getId())
				.claim("email", user.getEmail())
				.claim("role", user.getRole().name())
				.issuedAt(now)
				.expiration(expiresAt)
				.signWith(secretKey)
				.compact();
	}

	public boolean validateToken(String token) {
		parseClaims(token);
		return true;
	}

	public Long getUserId(String token) {
		Number userId = parseClaims(token).get("userId", Number.class);
		return userId.longValue();
	}

	public String getEmail(String token) {
		return parseClaims(token).get("email", String.class);
	}

	public String getRole(String token) {
		return parseClaims(token).get("role", String.class);
	}

	private Claims parseClaims(String token) {
		try {
			return Jwts.parser()
					.verifyWith(secretKey)
					.build()
					.parseSignedClaims(token)
					.getPayload();
		} catch (ExpiredJwtException exception) {
			throw new BusinessException(ErrorCode.EXPIRED_TOKEN);
		} catch (JwtException | IllegalArgumentException exception) {
			throw new BusinessException(ErrorCode.INVALID_TOKEN);
		}
	}
}
