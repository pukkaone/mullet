package org.springframework.petclinic.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Handles request for welcome page.
 */
@Controller
public class WelcomeController {

	/**
	 * Render welcome page.
	 */
	@RequestMapping("/")
	public String welcomeHandler() {
		return "Welcome";
	}
}
