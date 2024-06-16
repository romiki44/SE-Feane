package com.example.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aspectj.weaver.ast.Not;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)	
	private Long id;
	
	@NotEmpty
	private String password;
	
	@Email(message="Invalid Email")
	@NotEmpty
	@Column(unique = true)
	private String email;
	
	@NotEmpty
	private String firstName;
	
	@NotEmpty
	private String lastName;	
	
	@NotEmpty
	private String gender;
	
	//@NotEmpty - sposobi chybu, neda sa pouzit pri LocalDateTime!
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private LocalDate dateOfBirth;
	
	private String photo;
	
	private String role;
	
	@OneToMany(mappedBy = "account")
	private List<Post> posts;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name="account_authority", 
			joinColumns = {@JoinColumn(name="account_id", referencedColumnName = "id")},
			inverseJoinColumns = {@JoinColumn(name="authority_id", referencedColumnName = "id")})
	private Set<Authority> authorities=new HashSet<>();	
}
