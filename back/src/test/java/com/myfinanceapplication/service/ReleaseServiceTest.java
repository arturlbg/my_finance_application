package com.myfinance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.myfinance.model.repository.ReleaseRepositoryTest;
import com.myfinanceapplication.exception.BussinessRuleException;
import com.myfinanceapplication.model.entity.Release;
import com.myfinanceapplication.model.entity.User;
import com.myfinanceapplication.model.enums.StatusRelease;
import com.myfinanceapplication.model.enums.TypeRelease;
import com.myfinanceapplication.model.repository.ReleaseRepository;
import com.myfinanceapplication.service.impl.ReleaseServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ReleaseServiceTest {

	@SpyBean
	ReleaseServiceImpl service;
	@MockBean
	ReleaseRepository repository;
	
	@Test
	public void shouldSaveRelease() {
		Release releaseToSave = ReleaseRepositoryTest.createRelease();
		doNothing().when(service).valid(releaseToSave);
		
		Release releaseSaved = ReleaseRepositoryTest.createRelease();
		releaseSaved.setId(1l);
		releaseSaved.setStatus(StatusRelease.PENDING);
		when(repository.save(releaseToSave)).thenReturn(releaseSaved);
		
		Release release = service.save(releaseToSave);
		
		assertThat( release.getId() ).isEqualTo(releaseSaved.getId());
		assertThat(release.getStatus()).isEqualTo(StatusRelease.PENDING);
	}
	
	@Test
	public void shouldNotSaveReleaseWhenThereIsValidationError() {
		Release releaseToSave = ReleaseRepositoryTest.createRelease();
		doThrow( BussinessRuleException.class ).when(service).valid(releaseToSave);
		
		catchThrowableOfType( () -> service.save(releaseToSave), BussinessRuleException.class );
		verify(repository, never()).save(releaseToSave);
	}
	
	@Test
	public void shouldUpdateRelease() {
		Release releaseSaved = ReleaseRepositoryTest.createRelease();
		releaseSaved.setId(1l);
		releaseSaved.setStatus(StatusRelease.PENDING);

		doNothing().when(service).valid(releaseSaved);
		
		when(repository.save(releaseSaved)).thenReturn(releaseSaved);
		
		service.update(releaseSaved);
		
		verify(repository, times(1)).save(releaseSaved);
		
	}
	
	@Test
	public void shouldThrowAnErrorWhenTryingtoUpdateReleaseThatHasNotBeenSaved() {
		Release release = ReleaseRepositoryTest.createRelease();
		
		catchThrowableOfType( () -> service.update(release), NullPointerException.class );
		verify(repository, never()).save(release);
	}
	
	@Test
	public void shouldDeleteRelease() {
		Release release = ReleaseRepositoryTest.createRelease();
		release.setId(1l);
		
		service.delete(release);
		
		verify( repository ).delete(release);
	}
	
	@Test
	public void shouldThrowAnErrorWhenTryingToDeleteAReleaseThatHasNotSaved() {
		Release release = ReleaseRepositoryTest.createRelease();
		
		catchThrowableOfType( () -> service.delete(release), NullPointerException.class );
		
		verify( repository, never() ).delete(release);
	}
	
	
	@Test
	public void shouldFilterReleases() {
		Release release = ReleaseRepositoryTest.createRelease();
		release.setId(1l);
		
		List<Release> lista = Arrays.asList(release);
		when( repository.findAll(any(Example.class)) ).thenReturn(lista);
		
		List<Release> result = service.search(release);
		
		assertThat(result)
			.isNotEmpty()
			.hasSize(1)
			.contains(release);
		
	}
	
	@Test
	public void shouldUpdateReleaseStatusFromARelease() {
		Release release = ReleaseRepositoryTest.createRelease();
		release.setId(1l);
		release.setStatus(StatusRelease.PENDING);
		
		StatusRelease newStatus = StatusRelease.ACTIVATED;
		doReturn(release).when(service).update(release);
		
		service.updateStatus(release, newStatus);
		
		assertThat(release.getStatus()).isEqualTo(newStatus);
		verify(service).update(release);
		
	}
	
	@Test
	public void shouldGetAReleaseById() {
		Long id = 1l;
		
		Release release = ReleaseRepositoryTest.createRelease();
		release.setId(id);
		
		when(repository.findById(id)).thenReturn(Optional.of(release));
		
		Optional<Release> result =  service.getById(id);
		
		assertThat(result.isPresent()).isTrue();
	}
	
	@Test
	public void shouldReturnEmptyWhenTheReleaseDoNotExist() {
		Long id = 1l;
		
		Release release = ReleaseRepositoryTest.createRelease();
		release.setId(id);
		
		when( repository.findById(id) ).thenReturn( Optional.empty() );

		Optional<Release> result =  service.getById(id);
		
		assertThat(result.isPresent()).isFalse();
	}
	
	@Test
	public void shouldThrowErrorsWhenTringToValidateARelease() {
		Release release = new Release();
		
		Throwable error = Assertions.catchThrowable( () -> service.valid(release) );
		assertThat(error).isInstanceOf(BussinessRuleException.class).hasMessage("Insert a Valid Description.");
		
		release.setDescription("");
		
		error = Assertions.catchThrowable( () -> service.valid(release) );
		assertThat(error).isInstanceOf(BussinessRuleException.class).hasMessage("Insert a Valid Description.");
		
		release.setDescription("Salario");
		
		error = Assertions.catchThrowable( () -> service.valid(release) );
		assertThat(error).isInstanceOf(BussinessRuleException.class).hasMessage("Insert a Valid Month.");
		
		release.setYear(0);
		
		error = catchThrowable( () -> service.valid(release) );
		assertThat(error).isInstanceOf(BussinessRuleException.class).hasMessage("Insert a Valid Month.");
		
		release.setYear(13);
		
		error = catchThrowable( () -> service.valid(release) );
		assertThat(error).isInstanceOf(BussinessRuleException.class).hasMessage("Insert a Valid Month.");
		
		release.setMonth(1);
		
		error = catchThrowable( () -> service.valid(release) );
		assertThat(error).isInstanceOf(BussinessRuleException.class).hasMessage("Insert a Valid Year.");
		
		release.setYear(202);
		
		error = catchThrowable( () -> service.valid(release) );
		assertThat(error).isInstanceOf(BussinessRuleException.class).hasMessage("Insert a Valid Year.");
		
		release.setYear(2020);
		
		error = catchThrowable( () -> service.valid(release) );
		assertThat(error).isInstanceOf(BussinessRuleException.class).hasMessage("Insert a User.");
		
		release.setUser(new User());
		
		error = catchThrowable( () -> service.valid(release) );
		assertThat(error).isInstanceOf(BussinessRuleException.class).hasMessage("Insert a User.");
		
		release.getUser().setId(1l);
		
		error = catchThrowable( () -> service.valid(release) );
		assertThat(error).isInstanceOf(BussinessRuleException.class).hasMessage("Insert a Valid Value.");
		
		release.setValue(BigDecimal.ZERO);
		
		error = catchThrowable( () -> service.valid(release) );
		assertThat(error).isInstanceOf(BussinessRuleException.class).hasMessage("Insert a Valid Value.");
		
		release.setValue(BigDecimal.valueOf(1));
		
		error = catchThrowable( () -> service.valid(release) );
		assertThat(error).isInstanceOf(BussinessRuleException.class).hasMessage("Insert a Release Type.");
		
	}
	
	@Test
	public void shouldGetBalanceByUser() {
		Long idUser = 1l;
		
		when( repository
				.getBalanceByTypeReleaseAndUserAndStatus(idUser, TypeRelease.REVENUE, StatusRelease.ACTIVATED)) 
				.thenReturn(BigDecimal.valueOf(100));
		
		when( repository
				.getBalanceByTypeReleaseAndUserAndStatus(idUser, TypeRelease.EXPENSE, StatusRelease.ACTIVATED)) 
				.thenReturn(BigDecimal.valueOf(50));
		
		BigDecimal balance = service.getBalanceByUser(idUser);
		
		assertThat(balance).isEqualTo(BigDecimal.valueOf(50));
		
	}
	
}
