package mock.project.frontend.controller;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import mock.project.frontend.LocalDateDeserializer;
import mock.project.frontend.request.OrderDTO;
import mock.project.frontend.request.ProductDTO;
import mock.project.frontend.request.UserDTO;
import mock.project.frontend.request.UserDTOReponse;

@Controller
public class UserController {
	private Logger logger = Logger.getLogger(UserController.class);
	@Autowired
	private RestTemplate  restTemplate;
	
	@Value("${product.api.url}")
	private String productApi;

	@Value("${authentication.api.url}")
	private String authenticationApi;
	
	@Value("${user.api.url}")
	private String userApi;
	
	@Value("${admin.api.url}")
	private String adminApi;
	
	@Value("${order.api.url}")
	private String orderApi;
	
	//orders of user
	@GetMapping("/user/order")
	public String listOrdersUser(Model model,HttpServletRequest request) {
		logger.info("Loading order list views.....");
		String url = userApi+ "/order";	
		if(request.getAttribute("username")==null) {
			return "home-page";
		}
		try {
			ResponseEntity<OrderDTO[]> response = restTemplate.getForEntity(url, OrderDTO[].class);
			OrderDTO[] listOrders = response.getBody();
			model.addAttribute("listOrders", listOrders);
			return "check-order-page";
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
			return "error";
		}
	}
	//user infor
	@GetMapping("/user/info")
	public String getUserInfo(Model model,HttpSession session) {
		logger.info("Loading userinfo view.....");
		String username = (String)session.getAttribute("username");
		String url = userApi + "/info?username="+ username;
		UserDTO userInfo = restTemplate.getForObject(url, UserDTO.class);
		model.addAttribute("user", userInfo);
		return "customer-detail-page";
	}
	//update userinfo
	@PostMapping("/user/info")
	public String updateUserInfo(@ModelAttribute("user") UserDTO userDTO, Model model, HttpSession session) {
		String username = (String) session.getAttribute("username");
		String url = userApi + "/update?username=" + username;
		ResponseEntity<UserDTO> response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(userDTO),
				UserDTO.class);
		model.addAttribute("user", response.getBody());
		return "customer-detail-page";
	}
	//Thanh toán 
	@PostMapping("/order/check")
	public String makePayment(@CookieValue(value = "cookieProduct", defaultValue = "defaultCookieValue") String cookieProduct,
			Model model, HttpServletRequest request, HttpSession session) {
		String decodeCookieProduct = new String(Base64.getDecoder().decode(cookieProduct.getBytes()));
		GsonBuilder gsonBuilder = new GsonBuilder();
		Type productListType = new TypeToken<List<ProductDTO>>() {}.getType();
		List<ProductDTO> listItemCart = gsonBuilder.setPrettyPrinting()
				.registerTypeAdapter(LocalDate.class, new LocalDateDeserializer()).create()
				.fromJson(decodeCookieProduct, productListType);
		
		return "order-view-page";
	}

}
