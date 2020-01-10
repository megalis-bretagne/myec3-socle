package org.myec3.socle.webapp.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class ErrorController {

	// for 403 access denied page
	@RequestMapping(value = "/errorAccess", method = { RequestMethod.POST, RequestMethod.GET })
	public String accesssDenied(Principal user) {
		return "redirect:resources/errorAccess.html";

	}

	@ExceptionHandler({ PreAuthenticatedCredentialsNotFoundException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST) // 400
	@ResponseBody
	public String resolveBadRequestExceptions() {
		return "redirect:resources/errorOpenSso.html";
	}
}
