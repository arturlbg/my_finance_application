package com.myfinanceapplication.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.myfinanceapplication.model.entity.Release;
import com.myfinanceapplication.model.enums.StatusRelease;

public interface ReleaseService {

	Release save(Release release);
	
	Release update(Release release);
	
	void delete(Release release);
	
	List<Release> search( Release releaseFilter );
	
	void updateStatus(Release release, StatusRelease status);
	
	void valid(Release release);
	
	Optional<Release> getById(Long id);
	
	BigDecimal getBalanceByUser(Long id);
}