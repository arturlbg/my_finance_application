package com.myfinanceapplication.service;

import java.util.Optional;

import com.myfinanceapplication.model.entity.User;

public interface UserService {

	User authentic(String email, String password);
	
	User saveUser(User user);
	
	void validEmail(String email);
	
	Optional<User> getById(Long id);
	
}
