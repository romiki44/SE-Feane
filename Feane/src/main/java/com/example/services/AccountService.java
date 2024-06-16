package com.example.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.models.Account;
import com.example.models.Authority;
import com.example.repositories.AccountRepository;
import com.example.util.constant.Roles;

@Service
public class AccountService implements UserDetailsService {
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public Account save(Account account) {
		account.setPassword(passwordEncoder.encode(account.getPassword()));
		if(account.getRole()==null) 
			account.setRole(Roles.USER.getRole());
		if(account.getPhoto()==null) {
			account.setPhoto("images/person.png");
		}
		return accountRepository.save(account);
	}
	
	public List<Account> getAll() {
		return accountRepository.findAll();
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		//katastrofa, zase som nenasiel findOneBYEmailIgnoreCase(), tak som hladal a nasiel som toto:
		//https://docs.spring.io/spring-data/jpa/reference/repositories/query-by-example.html
		//ak pridam do repository potrebnu funkciu, ide to aj takto, ale neviem dat IgnoreCase!
		//Optional<Account> optAccount=accountRepository.findOneByEmail(email);
		
		Account exampleAccount=new Account();
		exampleAccount.setEmail(email);		
		ExampleMatcher matcher=ExampleMatcher.matching().withIgnoreCase();
		Example<Account> example=Example.of(exampleAccount, matcher);
		
		Optional<Account> optAccount=accountRepository.findOne(example);
		
		//account not found
		if(!optAccount.isPresent()) {
			throw new UsernameNotFoundException("Account with email '" + email + "'' not found");
		}
		
		//account exists!
		Account account=optAccount.get();
		List<GrantedAuthority> grantedAuthorities=new ArrayList<GrantedAuthority>();
		grantedAuthorities.add(new SimpleGrantedAuthority(account.getRole()));
		
		//treba nastavit lazy_loading  cez to application.properties, inac to krachne
		//inac je to katastrofalne neprehladne a komplikovane, toto hibernate...zlaty dobry EF!!!
		//spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true
		for(Authority auth: account.getAuthorities()) {
			grantedAuthorities.add(new SimpleGrantedAuthority(auth.getName()));
		}
		
		return new User(account.getEmail(), account.getPassword(), grantedAuthorities);
	}
	
	public Optional<Account> findByEmail(String email) {
		return accountRepository.findOneByEmail(email);
	}
	
	public Optional<Account> findById(Long Id) {
		return accountRepository.findById(Id);
	}
}
