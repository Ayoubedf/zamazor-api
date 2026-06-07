package com.zamazor.market.security.crypto;

import com.zamazor.market.security.config.JwtProperties;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.HexFormat;

@Component
@RequiredArgsConstructor
public class JwtKeyProvider {

	private final JwtProperties jwtProperties;

	public SecretKey getAccessKey() {
		byte[] keyBytes = HexFormat.of().parseHex(jwtProperties.access().secret());
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public SecretKey getRefreshKey() {
		byte[] keyBytes = HexFormat.of().parseHex(jwtProperties.refresh().secret());
		return Keys.hmacShaKeyFor(keyBytes);
	}
}