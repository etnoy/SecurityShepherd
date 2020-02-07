package org.owasp.securityshepherd;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.owasp.securityshepherd.auth.ShepherdUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.WebApplicationContext;


@Configuration
@EnableWebSecurity
public class ShepherdSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private WebApplicationContext applicationContext;
    
    private ShepherdUserDetailsService userDetailsService;

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void completeSetup() {
        userDetailsService = applicationContext.getBean(ShepherdUserDetailsService.class);
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(encoder())
            .and()
            .authenticationProvider(authenticationProvider())
            .jdbcAuthentication()
            .dataSource(dataSource);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
            .antMatchers("/resources/**");
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/login")
            .permitAll()
            .and()
            .formLogin()
            .permitAll()
            .and()
            .csrf()
            .disable();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(11);
    }

}