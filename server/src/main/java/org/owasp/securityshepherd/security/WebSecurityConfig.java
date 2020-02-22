package org.owasp.securityshepherd.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(final HttpSecurity http) throws Exception {

	    http.csrf().disable()
	    .authorizeRequests()
	    .antMatchers("/api/v1/use/registration").permitAll()  //context path here
	    .anyRequest().anonymous();
	    
		//http.authorizeRequests().antMatchers("/register").permitAll().antMatchers(HttpMethod.POST, "/api/v1/user/registration").permitAll().antMatchers("/","/index").hasAnyRole("admin", "player").and().formLogin()
		//		.loginPage("/login").permitAll().and().logout().permitAll();
	}

	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder(11);
	}

}