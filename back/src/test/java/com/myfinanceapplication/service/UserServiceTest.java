package com.myfinance.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.myfinanceapplication.exception.AuthenticationError;
import com.myfinanceapplication.exception.BussinessRuleException;
import com.myfinanceapplication.model.entity.User;
import com.myfinanceapplication.model.repository.UserRepository;
import com.myfinanceapplication.service.impl.UserServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UserServiceTest {

	@SpyBean
	UserServiceImpl service;
	
	@MockBean
	UserRepository repository;
	
	@Test
	public void shouldSaveUser() {
		Mockito.doNothing().when(service).validEmail(Mockito.anyString());
		User user = User.builder()
					.id(1l)
					.name("name")
					.email("email@email.com")
					.password("password").build();
		
		Mockito.when(repository.save(Mockito.any(User.class))).thenReturn(user);
		
		User userSaved = service.saveUser(new User());
		
		Assertions.assertThat(userSaved).isNotNull();
		Assertions.assertThat(userSaved.getId()).isEqualTo(1l);
		Assertions.assertThat(userSaved.getName()).isEqualTo("nome");
		Assertions.assertThat(userSaved.getEmail()).isEqualTo("email@email.com");
		Assertions.assertThat(userSaved.getPassword()).isEqualTo("password");
		
	}
	
	@Test
	public void shouldNotSaveAUserWithAlreadyRegisteredEmail() {
		String email = "email@email.com";
		User user = User.builder().email(email).build();
		Mockito.doThrow(BussinessRuleException.class).when(service).validEmail(email);
		
		org.junit.jupiter.api.Assertions
			.assertThrows(BussinessRuleException.class, () -> service.saveUser(user) ) ;
		
		Mockito.verify( repository, Mockito.never() ).save(user);
	}
	
	@Test
	public void shouldAuthenticUserSuccessfully() {
		String email = "email@email.com";
		String password = "password";
		
		User user = User.builder().email(email).password(password).id(1l).build();
		Mockito.when( repository.findByEmail(email) ).thenReturn(Optional.of(user));
		
		User result = service.authentic(email, password);
		
		Assertions.assertThat(result).isNotNull();
		
	}
	
	@Test
	public void shouldThrowErrorWhenNotFoundUserRegisteredWithInsertedEmail() {
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
		Throwable exception = Assertions.catchThrowable( () -> service.authentic("email@email.com", "password") );
		
		Assertions.assertThat(exception)
			.isInstanceOf(AuthenticationError.class)
			.hasMessage("User not found to inserted email.");
	}
	
	@Test
	public void shouldThrowErrorWhenPasswordDoNotMatch() {
		String password = "password";
		User user = User.builder().email("email@email.com").password(password).build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(user));
		
		Throwable exception = Assertions.catchThrowable( () ->  service.authentic("email@email.com", "123") );
		Assertions.assertThat(exception).isInstanceOf(AuthenticationError.class).hasMessage("invalid password.");
		
	}
	
	@Test
	public void shouldValidEmail() {
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		
		service.validEmail("email@email.com");
	}
	
	@Test
	public void shouldThrowErrorAValidEmailWhenThereIsNoEmailRegistered() {
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		org.junit.jupiter.api.Assertions
			.assertThrows(BussinessRuleException.class, () -> service.validEmail("email@email.com"));
	}
}
