package com.myfinanceapplication.service;

import com.myfinanceapplication.model.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

public interface JwtService {

	String generateToken(User user);
	
	Claims getClaims(String token) throws ExpiredJwtException;
	
	boolean isValidToken(String token);
	
	String getUserLogin( String token );
}
