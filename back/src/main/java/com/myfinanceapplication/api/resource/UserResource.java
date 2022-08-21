package com.myfinanceapplication.api.resource;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myfinanceapplication.api.dto.TokenDTO;
import com.myfinanceapplication.api.dto.UserDTO;
import com.myfinanceapplication.exception.AuthenticationError;
import com.myfinanceapplication.exception.BussinessRuleException;
import com.myfinanceapplication.model.entity.User;
import com.myfinanceapplication.service.JwtService;
import com.myfinanceapplication.service.ReleaseService;
import com.myfinanceapplication.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserResource {

	private final UserService service;
	private final ReleaseService releaseService;
	private final JwtService jwtService;
	
	@PostMapping("/authentic")
	public ResponseEntity<?> authentic( @RequestBody UserDTO dto ) {
		try {
			User userAuthenticated = service.authentic(dto.getEmail(), dto.getPassword());
			String token = jwtService.generateToken(userAuthenticated);
			TokenDTO tokenDTO = new TokenDTO( userAuthenticated.getName(), token);
			return ResponseEntity.ok(tokenDTO);
		}catch (AuthenticationError e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping
	public ResponseEntity save( @RequestBody UserDTO dto ) {
		
		User user = User.builder()
					.name(dto.getName())
					.email(dto.getEmail())
					.password(dto.getPassword()).build();
		
		try {
			User savedUser = service.saveUser(user);
			return new ResponseEntity(savedUser, HttpStatus.CREATED);
		}catch (BussinessRuleException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
	}
	
	@GetMapping("{id}/balance")
	public ResponseEntity getBalance( @PathVariable("id") Long id ) {
		Optional<User> user = service.getById(id);
		
		if(!user.isPresent()) {
			return new ResponseEntity( HttpStatus.NOT_FOUND );
		}
		
		BigDecimal balance = releaseService.getBalanceByUser(id);
		return ResponseEntity.ok(balance);
	}

}
