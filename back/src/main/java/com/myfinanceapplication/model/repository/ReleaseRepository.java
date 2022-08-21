package com.myfinanceapplication.model.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.myfinanceapplication.model.entity.Release;
import com.myfinanceapplication.model.enums.StatusRelease;
import com.myfinanceapplication.model.enums.TypeRelease;

public interface ReleaseRepository extends JpaRepository<Release, Long> {
	@Query( value = 
			  " select sum(l.value) from Release l join l.user u "
			+ " where u.id = :idUser and l.type =:type and l.status = :status group by u " )
	BigDecimal getBalanceByTypeReleaseAndUserAndStatus(
			@Param("idUser") Long idUser, 
			@Param("type") TypeRelease type,
			@Param("status") StatusRelease status);
	
}
