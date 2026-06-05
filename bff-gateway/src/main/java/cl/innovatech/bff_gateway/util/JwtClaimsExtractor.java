package cl.innovatech.bff_gateway.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.Optional;

public final class JwtClaimsExtractor {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private JwtClaimsExtractor() {
	}

	public static Optional<String> extractEmail(String authorizationHeader) {
		Optional<String> email = extractClaim(authorizationHeader, "email");
		if (email.isPresent()) {
			return email;
		}
		return extractClaim(authorizationHeader, "preferred_username")
			.filter(u -> u.contains("@"));
	}

	public static Optional<String> extractPreferredUsername(String authorizationHeader) {
		return extractClaim(authorizationHeader, "preferred_username");
	}

	private static Optional<String> extractClaim(String authorizationHeader, String claim) {
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			return Optional.empty();
		}
		String token = authorizationHeader.substring(7).trim();
		String[] parts = token.split("\\.");
		if (parts.length < 2) {
			return Optional.empty();
		}
		try {
			byte[] decoded = Base64.getUrlDecoder().decode(parts[1]);
			JsonNode node = MAPPER.readTree(decoded);
			JsonNode value = node.get(claim);
			if (value != null && !value.isNull() && value.isTextual()) {
				return Optional.of(value.asText());
			}
		} catch (Exception ignored) {
			return Optional.empty();
		}
		return Optional.empty();
	}
}
