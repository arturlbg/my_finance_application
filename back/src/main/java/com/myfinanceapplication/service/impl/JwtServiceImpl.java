package com.myfinanceapplication.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.myfinanceapplication.model.entity.User;
import com.myfinanceapplication.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtServiceImpl implements JwtService {
	
	@Value("${jwt.expiration}")
	private String expiration;
	
	@Value("${jwt.signature-key}")
	private String signatureKey;

	@Override
	public String generateToken(User user) {
		long exp = Long.valueOf(expiration);
		LocalDateTime dateHourExpiration = LocalDateTime.now().plusMinutes(exp);
		Instant instant = dateHourExpiration.atZone( ZoneId.systemDefault() ).toInstant();
		java.util.Date data = Date.from(instant);
		
		String hourExpirationToken = dateHourExpiration.toLocalTime()
				.format(DateTimeFormatter.ofPattern("HH:mm"));
		
		String token = Jwts
							.builder()
							.setExpiration(data)
							.setSubject(user.getEmail())
							.claim("userid", user.getId())
							.claim("name", user.getName())
							.claim("hourExpiration", hourExpirationToken)
							.signWith( SignatureAlgorithm.HS512 , signatureKey )
							.compact();
		
		return token;
	}

	@Override
	public Claims getClaims(String token) throws ExpiredJwtException {
		return Jwts
				.parser()
				.setSigningKey(signatureKey)
				.parseClaimsJws(token)
				.getBody();
	}

	@Override
	public boolean isValidToken(String token) {
		try {
			Claims claims = getClaims(token);
			java.util.Date dataEx = claims.getExpiration();
			LocalDateTime dataExpiracao = dataEx.toInstant()
					.atZone(ZoneId.systemDefault()).toLocalDateTime();
			boolean dataHoraAtualIsAfterDataExpiracao = LocalDateTime.now().isAfter(dataExpiracao);
			return !dataHoraAtualIsAfterDataExpiracao;
		}catch(ExpiredJwtException e) {
			return false;
		}
	}

	@Override
	public String getUserLogin(String token) {
		Claims claims = getClaims(token);
		return claims.getSubject();
	}

}
