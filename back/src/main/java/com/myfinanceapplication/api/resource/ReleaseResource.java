package com.myfinanceapplication.api.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.myfinanceapplication.api.dto.UpdateStatusDTO;
import com.myfinanceapplication.api.dto.ReleaseDTO;
import com.myfinanceapplication.exception.BussinessRuleException;
import com.myfinanceapplication.model.entity.Release;
import com.myfinanceapplication.model.entity.User;
import com.myfinanceapplication.model.enums.StatusRelease;
import com.myfinanceapplication.model.enums.TypeRelease;
import com.myfinanceapplication.service.ReleaseService;
import com.myfinanceapplication.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/releases")
@RequiredArgsConstructor
public class ReleaseResource {

	private final ReleaseService service;
	private final UserService userService;
	
	@GetMapping
	public ResponseEntity search(
			@RequestParam(value ="description" , required = false) String description,
			@RequestParam(value = "month", required = false) Integer month,
			@RequestParam(value = "year", required = false) Integer year,
			@RequestParam("user") Long idUser
			) {
		
		Release releaseFilter = new Release();
		releaseFilter.setDescription(description);
		releaseFilter.setMonth(month);
		releaseFilter.setYear(year);
		
		Optional<User> user = userService.getById(idUser);
		if(!user.isPresent()) {
			return ResponseEntity.badRequest().body("The query could not be performed. User not found for the given Id.");
		}else {
			releaseFilter.setUser(user.get());
		}
		
		List<Release> releases = service.search(releaseFilter);
		return ResponseEntity.ok(releases);
	}
	
	@GetMapping("{id}")
	public ResponseEntity obterRelease( @PathVariable("id") Long id ) {
		return service.getById(id)
					.map( release -> new ResponseEntity(convert(release), HttpStatus.OK) )
					.orElseGet( () -> new ResponseEntity(HttpStatus.NOT_FOUND) );
	}

	@PostMapping
	public ResponseEntity save( @RequestBody ReleaseDTO dto ) {
		try {
			Release entity = convert(dto);
			entity = service.save(entity);
			return new ResponseEntity(entity, HttpStatus.CREATED);
		}catch (BussinessRuleException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PutMapping("{id}")
	public ResponseEntity update( @PathVariable("id") Long id, @RequestBody ReleaseDTO dto ) {
		return service.getById(id).map( entity -> {
			try {
				Release release = convert(dto);
				release.setId(entity.getId());
				service.update(release);
				return ResponseEntity.ok(release);
			}catch (BussinessRuleException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet( () ->
			new ResponseEntity("Release not found in the database.", HttpStatus.BAD_REQUEST) );
	}
	
	@PutMapping("{id}/update-status")
	public ResponseEntity updateStatus( @PathVariable("id") Long id , @RequestBody UpdateStatusDTO dto ) {
		return service.getById(id).map( entity -> {
			StatusRelease statusSelected = StatusRelease.valueOf(dto.getStatus());
			
			if(statusSelected == null) {
				return ResponseEntity.badRequest().body("Unable to update release status, please submit a valid status.");
			}
			
			try {
				entity.setStatus(statusSelected);
				service.update(entity);
				return ResponseEntity.ok(entity);
			}catch (BussinessRuleException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		
		}).orElseGet( () ->
		new ResponseEntity("Release not found in the database.", HttpStatus.BAD_REQUEST) );
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity delete( @PathVariable("id") Long id ) {
		return service.getById(id).map( entity -> {
			service.delete(entity);
			return new ResponseEntity( HttpStatus.NO_CONTENT );
		}).orElseGet( () -> 
			new ResponseEntity("Release not found in the database.", HttpStatus.BAD_REQUEST) );
	}
	
	private ReleaseDTO convert(Release release) {
		return ReleaseDTO.builder()
					.id(release.getId())
					.description(release.getDescription())
					.value(release.getValue())
					.month(release.getMonth())
					.year(release.getYear())
					.status(release.getStatus().name())
					.type(release.getType().name())
					.user(release.getUser().getId())
					.build();
					
	}
	
	private Release convert(ReleaseDTO dto) {
		Release release = new Release();
		release.setId(dto.getId());
		release.setDescription(dto.getDescription());
		release.setYear(dto.getYear());
		release.setMonth(dto.getMonth());
		release.setValue(dto.getValue());
		
		User user = userService
			.getById(dto.getUser())
			.orElseThrow( () -> new BussinessRuleException("User not found for the given Id.") );
		
		release.setUser(user);

		if(dto.getType() != null) {
			release.setType(TypeRelease.valueOf(dto.getType()));
		}
		
		if(dto.getStatus() != null) {
			release.setStatus(StatusRelease.valueOf(dto.getStatus()));
		}
		
		return release;
	}
}
