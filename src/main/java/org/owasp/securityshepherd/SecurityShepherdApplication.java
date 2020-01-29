package org.owasp.securityshepherd;

import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SecurityShepherdApplication {

	public static void main(String[] args) throws Throwable {
		
		SpringApplication.run(SecurityShepherdApplication.class, args);
	}

}