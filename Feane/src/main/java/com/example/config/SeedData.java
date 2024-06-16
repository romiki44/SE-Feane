package com.example.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.models.Account;
import com.example.models.Authority;
import com.example.models.Post;
import com.example.services.AccountService;
import com.example.services.AuthorityService;
import com.example.services.PostService;
import com.example.util.constant.Privillages;
import com.example.util.constant.Roles;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

@Component
public class SeedData implements CommandLineRunner {
	@Autowired
	private PostService postService;
	@Autowired 
	private AccountService accountService;
	@Autowired
	private AuthorityService authorityService;

	@Override
	public void run(String... args) throws Exception {
		System.out.println("SeedData started...");		
		
		for(Privillages privillage: Privillages.values()) {
			Authority authority=new Authority();
			authority.setId(privillage.getId());
			authority.setName(privillage.getPrivillage());
			authorityService.save(authority);
		}
		
		Set<Authority> authorities=new HashSet<Authority>(authorityService.getAll());
		
		Account account01=new Account();
		account01.setEmail("tom.jones@mail.com");
		account01.setFirstName("Tom");
		account01.setLastName("Jones");
		account01.setPassword("pwd123");
		account01.setGender("M");
		account01.setDateOfBirth(LocalDate.of(1980, 5,20));
		account01.setRole(Roles.USER.getRole());
		
		Account account02=new Account();
		account02.setEmail("johny.cash@mail.com");
		account02.setFirstName("Johny");
		account02.setLastName("Cash");
		account02.setPassword("pwd123");
		account02.setGender("M");
		account02.setDateOfBirth(LocalDate.of(1985,7,12));
		account02.setRole(Roles.USER.getRole());
		
		Account account03=new Account();
		account03.setEmail("james.kirk@mail.com");
		account03.setFirstName("James");
		account03.setLastName("Kirk");
		account03.setPassword("pwd123");
		account03.setGender("M");
		account03.setDateOfBirth(LocalDate.of(1992, 12,25));
		account03.setRole(Roles.ADMIN.getRole());
		account03.setAuthorities(authorities);
		
		Account account04=new Account();
		account04.setEmail("eva.mendes@mail.com");
		account04.setFirstName("Eva");
		account04.setLastName("Mendes");
		account04.setPassword("pwd123");
		account04.setGender("F");
		account04.setDateOfBirth(LocalDate.of(1999, 2,2));
		account04.setRole(Roles.EDITOR.getRole());
						
		accountService.save(account01);
		accountService.save(account02);
		accountService.save(account03);
		accountService.save(account04);
				
		List<Post> posts = postService.getAll();
		if (posts.size() == 0) {
			Post post01 = new Post();
			post01.setTitle("Post Title 01");
			post01.setBody("Post Body 01");
			post01.setAccount(account01);
			postService.save(post01);
			
			Post post02 = new Post();
			post02.setTitle("Post Title 02");
			post02.setBody("Post Body 02");
			post02.setAccount(account02);
			postService.save(post02);
		}
		
		System.out.println("SeedData finished ok...");
	}

}
