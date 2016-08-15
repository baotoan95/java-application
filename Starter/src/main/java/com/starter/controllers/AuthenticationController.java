package com.starter.controllers;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.starter.dao.UserDAO;
import com.starter.forms.UserAuthenticationForm;
import com.starter.forms.UserRegistrationForm;
import com.starter.validators.UserRegistrationValidator;

@Controller
public class AuthenticationController extends BaseController {

	@Autowired
	private UserDAO						userDAO;

	@Autowired
	private PasswordEncoder				passwordEncoder;

	@Autowired
	private UserRegistrationValidator	userRegistrationValidator;

	@InitBinder("UserRegistrationValidator")
	public void initUserNameAvailableValidatorBinder(WebDataBinder binder) {
		binder.addValidators(this.userRegistrationValidator);
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Model model) {
		model.addAttribute("userAuthenticationForm", new UserAuthenticationForm());
		return "authentication.login";
	}

	@RequestMapping(value = "/registration", method = RequestMethod.GET)
	public String registration(Model model) {
		model.addAttribute("userRegistrationForm", new UserRegistrationForm());
		return "authentication.registration";
	}

	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public String register(HttpServletResponse response, Model model, @Valid @ModelAttribute("UserRegistrationValidator") UserRegistrationForm userRegistrationForm, BindingResult binding, RedirectAttributes attr) {

		if (binding.hasErrors()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			model.addAttribute("org.springframework.validation.BindingResult.userRegistrationForm", binding);
			model.addAttribute("userRegistrationForm", userRegistrationForm);
			return "authentication.registration";
		}

		userRegistrationForm.setPassword(this.passwordEncoder.encode(userRegistrationForm.getPassword()));
		this.userDAO.create(userRegistrationForm);

		return "redirect:/login";
	}

}
