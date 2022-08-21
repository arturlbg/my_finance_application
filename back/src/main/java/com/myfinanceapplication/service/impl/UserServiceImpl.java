package com.myfinanceapplication.service.impl;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myfinanceapplication.exception.AuthenticationError;
import com.myfinanceapplication.exception.BussinessRuleException;
import com.myfinanceapplication.model.entity.User;
import com.myfinanceapplication.model.repository.UserRepository;
import com.myfinanceapplication.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	private UserRepository repository;
	private PasswordEncoder encoder;
	
	public UserServiceImpl(
			UserRepository repository, 
			PasswordEncoder encoder) {
		super();
		this.repository = repository;
		this.encoder = encoder;
	}

	@Override
	public User authentic(String email, String password) {
		Optional<User> user = repository.findByEmail(email);
		
		if(!user.isPresent()) {
			throw new AuthenticationError("User not found for the email provided.");
		}
		
		boolean passwordsMatch = encoder.matches(password, user.get().getPassword());
		
		if(!passwordsMatch) {
			throw new AuthenticationError("Invalid password.");
		}

		return user.get();
	}

	@Override
	@Transactional
	public User saveUser(User user) {
		validEmail(user.getEmail());
		encryptPassword(user);
		return repository.save(user);
	}

	private void encryptPassword(User user) {
		String password = user.getPassword();
		String passwordEncrypt = encoder.encode(password);
		user.setPassword(passwordEncrypt);
	}

	@Override
	public void validEmail(String email) {
		boolean exist = repository.existsByEmail(email);
		if(exist) {
			throw new BussinessRuleException("There is already a user registered with this email.");
		}
	}

	@Override
	public Optional<User> getById(Long id) {
		return repository.findById(id);
	}

}
