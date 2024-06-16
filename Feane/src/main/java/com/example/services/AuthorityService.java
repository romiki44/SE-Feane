package com.example.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.models.Authority;
import com.example.repositories.AuthorityRepository;

@Service
public class AuthorityService {
	@Autowired
	private AuthorityRepository authorityRepository;
	
	public Authority save(Authority authority) {
		return authorityRepository.save(authority);
	}
	
	public Optional<Authority> fingById(Long id) {
		return authorityRepository.findById(id);
	}
	
	//toto vobec nechodi...absolutne nechapem preco!!!
	public List<Authority> getAll() {
		return authorityRepository.findAll();
	}
}
