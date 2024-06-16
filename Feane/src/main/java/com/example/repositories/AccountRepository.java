package com.example.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.models.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {	
	Optional<Account> findOneByEmail(String email);
}
