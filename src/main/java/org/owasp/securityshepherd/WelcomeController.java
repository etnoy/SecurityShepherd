package org.owasp.securityshepherd;

import java.util.Collections;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WelcomeController {

	@RequestMapping("/")
	public String index(Map<String, Object> model) {
		return "index";
	}
	
	@GetMapping("/login")
	public String login(Map<String, Object> model) {
		return "login";
	}

	@PostMapping("/login")
	public ModelAndView login(String loginData) {
		if (loginData.equals("")) {
			return new ModelAndView("success", Collections.singletonMap("login", "test"));
		} else {
			return new ModelAndView("failure", Collections.singletonMap("login", "test"));
		}
	}
}