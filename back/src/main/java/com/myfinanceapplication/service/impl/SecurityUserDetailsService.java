package com.myfinanceapplication.service.impl;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.myfinanceapplication.model.repository.UserRepository;

@Service
public class SecurityUserDetailsService implements UserDetailsService {
	
	private UserRepository userRepository;

	public SecurityUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		com.myfinanceapplication.model.entity.User userFound = userRepository
				.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Email não cadastrado."));
		
		return User.builder()
				.username(userFound.getEmail())
				.password(userFound.getPassword())
				.roles("USER")
				.build();
	}

}
