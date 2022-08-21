package com.myfinanceapplication.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myfinanceapplication.exception.BussinessRuleException;
import com.myfinanceapplication.model.entity.Release;
import com.myfinanceapplication.model.enums.StatusRelease;
import com.myfinanceapplication.model.enums.TypeRelease;
import com.myfinanceapplication.model.repository.ReleaseRepository;
import com.myfinanceapplication.service.ReleaseService;

@Service
public class ReleaseServiceImpl implements ReleaseService {
	
	private ReleaseRepository repository;
	
	public ReleaseServiceImpl(ReleaseRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional
	public Release save(Release release) {
		valid(release);
		release.setStatus(StatusRelease.PENDING);
		return repository.save(release);
	}

	@Override
	@Transactional
	public Release update(Release release) {
		Objects.requireNonNull(release.getId());
		valid(release);
		return repository.save(release);
	}

	@Override
	@Transactional
	public void delete(Release release) {
		Objects.requireNonNull(release.getId());
		repository.delete(release);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Release> search(Release releaseFilter) {
		Example example = Example.of( releaseFilter, 
				ExampleMatcher.matching()
					.withIgnoreCase()
					.withStringMatcher(StringMatcher.CONTAINING) );
		
		return repository.findAll(example);
	}

	@Override
	public void updateStatus(Release release, StatusRelease status) {
		release.setStatus(status);
		update(release);
	}

	@Override
	public void valid(Release release) {
		
		if(release.getDescription() == null || release.getDescription().trim().equals("")) {
			throw new BussinessRuleException("Informe uma Descrição válida.");
		}
		
		if(release.getMonth() == null || release.getMonth() < 1 || release.getMonth() > 12) {
			throw new BussinessRuleException("Informe um Mês válido.");
		}
		
		if(release.getYear() == null || release.getYear().toString().length() != 4 ) {
			throw new BussinessRuleException("Informe um Ano válido.");
		}
		
		if(release.getUser() == null || release.getUser().getId() == null) {
			throw new BussinessRuleException("Informe um Usuário.");
		}
		
		if(release.getValue() == null || release.getValue().compareTo(BigDecimal.ZERO) < 1 ) {
			throw new BussinessRuleException("Informe um value válido.");
		}
		
		if(release.getType() == null) {
			throw new BussinessRuleException("Informe um type de Lançamento.");
		}
	}

	@Override
	public Optional<Release> getById(Long id) {
		return repository.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal getBalanceByUser(Long id) {
		BigDecimal revenues = repository.getBalanceByTypeReleaseAndUserAndStatus(id, TypeRelease.REVENUE, StatusRelease.ACTIVATED);
		BigDecimal expenses = repository.getBalanceByTypeReleaseAndUserAndStatus(id, TypeRelease.EXPENSE, StatusRelease.ACTIVATED);
		
		if(revenues == null) {
			revenues = BigDecimal.ZERO;
		}
		
		if(expenses == null) {
			expenses = BigDecimal.ZERO;
		}
		
		return revenues.subtract(expenses);
	}

}
