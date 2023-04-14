package mock.project.frontend.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import mock.project.frontend.request.User;
import mock.project.frontend.request.UserDTO;
import mock.project.frontend.response.ResponseTransfer;

@Controller
public class LoginAndRegisterController {
	
	private Logger logger = Logger.getLogger(LoginAndRegisterController.class);
	
	@Autowired
	private RestTemplate restTemplate;

	@Value("${user.api.url}")
	private String userApi;

	@Value("${authentication.api.url}")
	private String authenticationApi;

	// register view
	@GetMapping("/register")
	public String registerViews(Model model) {
		logger.info("Loading register form...");
		model.addAttribute("user", new UserDTO());
		return "register-page";
	}
	
	//register new member front-end
	@PostMapping("/register")
	public String registerNewMember(Model model, @ModelAttribute("user") @Valid UserDTO user, BindingResult bindingResult) {
			logger.info("Processing...");
        	String url = userApi + "/register";
        	ResponseEntity<String> response = restTemplate.postForEntity(url, user, String.class);
        	if(response.getBody().contains("Username has been used")) {
        		model.addAttribute("msg", response.getBody());
        		model.addAttribute("user", new UserDTO());
            	return "register-page";
        	}
        	model.addAttribute("msg", response.getBody());
        	return "login-page";
		}
	//check login
	@PostMapping("/login")
	public String checkLogin(@ModelAttribute("user") User user, HttpServletRequest request,
			HttpServletResponse response, Model model) throws UnsupportedEncodingException {
		String url = authenticationApi + "/login";
		ResponseEntity<String> token = restTemplate.postForEntity(url, user, String.class);
		if (token.getBody().contains("Incorrect")) {
				model.addAttribute("msg", token.getBody());
				return "login-page";
			} else {
				Cookie tokenCookie = new Cookie("Authorization", URLEncoder.encode("Bearer " + token.getBody(), "UTF-8"));
				request.getSession().setAttribute("username", user.getUsername());
				tokenCookie.setMaxAge(24 * 60 * 60);
				response.addCookie(tokenCookie);
				return "redirect:/";
			}
	}
	//load login form
	@GetMapping("/login")
	public String loginForm(Model model) {
			logger.info("Loading login form.....");
		model.addAttribute("user", new User());
		return "login-page";
	}
	
	//logout
	@GetMapping("/logout")
	public String logout(Model model, HttpServletRequest request, HttpServletResponse response,HttpSession session) {
		String url = authenticationApi + "/logout";
		String msg = restTemplate.getForObject(url, String.class);
		Cookie cookie = null;
		Cookie[] cookies = null;
		cookies = request.getCookies();
		session.removeAttribute("username");
		AdminController.jwt = null;
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				cookie = cookies[i];
				if ((cookie.getName()).compareTo("Authorization") == 0) {
					cookie.setMaxAge(0);
					response.addCookie(cookie);
					break;
				}
			}
			return "login-page";
		}
		return "error";
	}

}
