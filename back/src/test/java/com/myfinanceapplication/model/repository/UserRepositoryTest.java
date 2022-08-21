package com.myfinance.model.repository;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.myfinanceapplication.model.entity.User;
import com.myfinanceapplication.model.repository.UserRepository;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserRepositoryTest {
	
	@Autowired
	UserRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void shouldVerifyEmailExistence() {
		User user = createUser();
		entityManager.persist(user);
		
		boolean result = repository.existsByEmail("user@email.com");
		
		Assertions.assertThat(result).isTrue();
		
	}
	
	@Test
	public void shouldReturnFalseWhenThereIsNoRegisteredUserWithEmail() {
		boolean result = repository.existsByEmail("user@email.com");
		
		Assertions.assertThat(result).isFalse();
	}
	
	@Test
	public void shouldPersistUserInDataBase() {
		User user = createUser();
		
		User savedUser = repository.save(user);
		
		Assertions.assertThat(savedUser.getId()).isNotNull();
	}
	
	@Test
	public void shouldSearchUserWithEmail() {
		User user = createUser();
		entityManager.persist(user);
		
		Optional<User> result = repository.findByEmail("user@email.com");
		
		Assertions.assertThat( result.isPresent() ).isTrue();
		
	}
	
	@Test
	public void shouldReturnEmptyToSearchUserByEmailWhenNotExistingInBase() {
		Optional<User> result = repository.findByEmail("user@email.com");
		
		Assertions.assertThat( result.isPresent() ).isFalse();
		
	}
	
	public static User createUser() {
		return User
				.builder()
				.name("user")
				.email("user@email.com")
				.password("password")
				.build();
	}

}
