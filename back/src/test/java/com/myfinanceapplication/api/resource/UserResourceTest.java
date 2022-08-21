package com.myfinance.api.resource;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myfinanceapplication.api.dto.UserDTO;
import com.myfinanceapplication.service.UserService;
import com.myfinanceapplication.exception.AuthenticationError;
import com.myfinanceapplication.exception.BussinessRuleException;
import com.myfinanceapplication.model.entity.User;
import com.myfinanceapplication.service.ReleaseService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest( controllers = UserService.class )
@AutoConfigureMockMvc
public class UserResourceTest {
	
	static final String API = "/api/users";
	static final MediaType JSON = MediaType.APPLICATION_JSON;
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	UserService service;
	
	@MockBean
	ReleaseService releaseService;
	
	@Test
	public void shouldAuthenticateAnUser() throws Exception {
		String email = "user@email.com";
		String password = "123";
		
		UserDTO dto = UserDTO.builder().email(email).password(password).build();
		User user = User.builder().id(1l).email(email).password(password).build();
		Mockito.when( service.authentic(email, password) ).thenReturn(user);
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post( API.concat("/authentic") )
													.accept( JSON )
													.contentType( JSON )
													.content(json);
		
		
		mvc
			.perform(request)
			.andExpect( MockMvcResultMatchers.status().isOk()  )
			.andExpect( MockMvcResultMatchers.jsonPath("id").value(user.getId())  )
			.andExpect( MockMvcResultMatchers.jsonPath("name").value(user.getName())  )
			.andExpect( MockMvcResultMatchers.jsonPath("email").value(user.getEmail())  )
			
		;
		
	}
	
	@Test
	public void ShouldReturnBadRequestWhenGettingAuthenticationError() throws Exception {
		String email = "user@email.com";
		String password = "123";
		
		UserDTO dto = UserDTO.builder().email(email).password(password).build();
		Mockito.when( service.authentic(email, password) ).thenThrow(AuthenticationError.class);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post( API.concat("/authentic") )
													.accept( JSON )
													.contentType( JSON )
													.content(json);
		
		
		mvc
			.perform(request)
			.andExpect( MockMvcResultMatchers.status().isBadRequest()  );
			
		;
		
	}
	
	@Test
	public void mustCreateNewUser() throws Exception {
		String email = "user@email.com";
		String password = "123";
		
		UserDTO dto = UserDTO.builder().email("user@email.com").password("123").build();
		User user = User.builder().id(1l).email(email).password(password).build();
		
		Mockito.when( service.saveUser(Mockito.any(User.class)) ).thenReturn(user);
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post( API  )
													.accept( JSON )
													.contentType( JSON )
													.content(json);
		
		
		mvc
			.perform(request)
			.andExpect( MockMvcResultMatchers.status().isCreated()  )
			.andExpect( MockMvcResultMatchers.jsonPath("id").value(user.getId())  )
			.andExpect( MockMvcResultMatchers.jsonPath("name").value(user.getName())  )
			.andExpect( MockMvcResultMatchers.jsonPath("email").value(user.getEmail())  )
			
		;
		
	}
	
	@Test
	public void ShouldReturnBadRequestWhenTryingToCreateAnInvalidUser() throws Exception {
		String email = "user@email.com";
		String password = "123";
		
		UserDTO dto = UserDTO.builder().email("user@email.com").password("123").build();
		
		Mockito.when( service.saveUser(Mockito.any(User.class)) ).thenThrow(BussinessRuleException.class);
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post( API  )
													.accept( JSON )
													.contentType( JSON )
													.content(json);
		
		
		mvc
			.perform(request)
			.andExpect( MockMvcResultMatchers.status().isBadRequest()  );
			
		;
		
	}
	
	@Test
	public void shouldGetUserBalance() throws Exception {
		BigDecimal balance = BigDecimal.valueOf(10);
		User user = User.builder().id(1l).email("user@email.com").password( "123").build();
		Mockito.when(service.getById(1l)).thenReturn(Optional.of(user));
		Mockito.when(releaseService.getBalanceByUser(1l)).thenReturn(balance);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.get( API.concat("/1/balance")  )
													.accept( JSON )
													.contentType( JSON );
		mvc
			.perform(request)
			.andExpect( MockMvcResultMatchers.status().isOk() )
			.andExpect( MockMvcResultMatchers.content().string("10") );
		
	}
	
	@Test
	public void shouldReturnResourceNotFoundWhenUserNotExistGettingBalance() throws Exception {
		Mockito.when(service.getById(1l)).thenReturn(Optional.empty());
		
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.get( API.concat("/1/balance")  )
													.accept( JSON )
													.contentType( JSON );
		mvc
			.perform(request)
			.andExpect( MockMvcResultMatchers.status().isNotFound() );
		
	}

}
