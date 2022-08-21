package com.myfinance.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.myfinanceapplication.model.entity.Release;
import com.myfinanceapplication.model.enums.StatusRelease;
import com.myfinanceapplication.model.enums.TypeRelease;
import com.myfinanceapplication.model.repository.ReleaseRepository;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class ReleaseRepositoryTest {

	@Autowired
	ReleaseRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void shouldSaveRelease() {
		Release release = createRelease();
		
		release = repository.save(release);
		
		assertThat(release.getId()).isNotNull();
	}

	@Test
	public void shouldDeleteRelease() {
		Release release = createAndPersistRelease();
		
		release = entityManager.find(Release.class, release.getId());
		
		repository.delete(release);
		
		Release releaseNonexistent = entityManager.find(Release.class, release.getId());
		assertThat(releaseNonexistent).isNull();
	}

	
	@Test
	public void shouldUpdateRelease() {
		Release release = createAndPersistRelease();
		
		release.setYear(2018);
		release.setDescription("update test");
		release.setStatus(StatusRelease.CANCELED);
		
		repository.save(release);
		
		Release releaseAtualizado = entityManager.find(Release.class, release.getId());
		
		assertThat(releaseAtualizado.getYear()).isEqualTo(2018);
		assertThat(releaseAtualizado.getDescription()).isEqualTo("update test");
		assertThat(releaseAtualizado.getStatus()).isEqualTo(StatusRelease.CANCELED);
	}
	
	@Test
	public void shouldSearchReleaseById() {
		Release release = createAndPersistRelease();
		
		Optional<Release> releaseFound = repository.findById(release.getId());
		
		assertThat(releaseFound.isPresent()).isTrue();
	}

	private Release createAndPersistRelease() {
		Release release = createRelease();
		entityManager.persist(release);
		return release;
	}
	
	public static Release createRelease() {
		return Release.builder()
									.year(2022)
									.month(1)
									.description("test release")
									.value(BigDecimal.valueOf(10))
									.type(TypeRelease.REVENUE)
									.status(StatusRelease.PENDING)
									.registrationDate(LocalDate.now())
									.build();
	}
	
	
	
	
	
}
