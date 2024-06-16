package com.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.util.constant.Privillages;
import com.example.util.constant.Roles;

//***
//Pozor, verzia WebSecurity>6.0 je uplne ina ako v kurze, vid link nizsie...
//https://docs.spring.io/spring-security/reference/5.8/migration/servlet/config.html
//***
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	private static final String[] WHITELIST= {
			"/",
			"/login",
			"/book",
			"/register",
			"/db-console/**",			
			"/css/**",
			"/fonts/**",
			"/images/**",
			"/uploads/**",
			"/js/**"
			//"/api/v1/home/**",
	};

    @Bean
    static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
		.authorizeHttpRequests((authz) -> authz
        	.requestMatchers(WHITELIST).permitAll()  //.anyRequest()
        	.requestMatchers("/profile/**").authenticated()
        	.requestMatchers("/posts/**").authenticated()
        	.requestMatchers("/api/v1/**").authenticated()
        	.requestMatchers("/admin/**").hasRole("ADMIN")
        	.requestMatchers("/editor/**").hasAnyRole("ADMIN", "EDITOR")
        	.requestMatchers("/admin/**").hasAuthority(Privillages.ACCESS_ADMIN_PANEL.getPrivillage()))		
		.formLogin(login->login
			.loginPage("/login")
			.loginProcessingUrl("/login")
			.usernameParameter("email")
			.passwordParameter("password")
			.defaultSuccessUrl("/", true)
			.failureUrl("/login?error")
			.permitAll())
		.logout(logout->logout
			.logoutUrl("/logout")
			.logoutSuccessUrl("/"))
		.httpBasic(Customizer.withDefaults())
		.rememberMe(m->m.rememberMeParameter("remember-me"))
		.csrf(c->c.disable())
		.headers(h->h.frameOptions(f->f.disable()));
		
		return http.build();			
	}
}
