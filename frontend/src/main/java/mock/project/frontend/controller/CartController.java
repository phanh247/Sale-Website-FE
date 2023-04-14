package mock.project.frontend.controller;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import mock.project.frontend.LocalDateDeserializer;
import mock.project.frontend.entities.Items;
import mock.project.frontend.entities.Products;
import mock.project.frontend.request.OrderDTO;
import mock.project.frontend.request.OrderRequest;
import mock.project.frontend.request.ProductDTO;
import mock.project.frontend.request.UserDTO;
import mock.project.frontend.request.UserDTOReponse;

@Controller
public class CartController {

	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${user.api.url}")
	private String userApi;

	@GetMapping("/cart")
	public String viewCart(
			@CookieValue(value = "cookieProduct", defaultValue = "defaultCookieValue") String cookieProduct,
			Model model, HttpServletRequest request, HttpSession session) {
		String decodeCookieProduct = new String(Base64.getDecoder().decode(cookieProduct.getBytes()));
		GsonBuilder gsonBuilder = new GsonBuilder();
		Type productListType = new TypeToken<List<ProductDTO>>() {}.getType();
		List<ProductDTO> listItemCart = gsonBuilder.setPrettyPrinting()
				.registerTypeAdapter(LocalDate.class, new LocalDateDeserializer()).create()
				.fromJson(decodeCookieProduct, productListType);

		System.out.println("json /n" + decodeCookieProduct);
//		for (ProductDTO productDTO : listItemCart) {
//			System.out.println(productDTO);
//		}
		model.addAttribute("listItemCart", listItemCart);
		String username = (String)session.getAttribute("username");
		if(username !=null) {
		String url = userApi + "/info?username="+ username;
		UserDTOReponse userInfo = restTemplate.getForObject(url, UserDTOReponse.class);
		model.addAttribute("user", userInfo);
		}else {
			model.addAttribute("user",new UserDTO());
		}
		return "cart-page";
	}
//	@GetMapping("/cart")
//	public String viewCart(
//			@CookieValue(value = "cookieProduct", defaultValue = "defaultCookieValue") String cookieProduct,
//			Model model, HttpServletRequest request, HttpSession session) {
//		
//		String decodeCookieProduct = new String(Base64.getDecoder().decode(cookieProduct.getBytes()));
//		GsonBuilder gsonBuilder = new GsonBuilder();
//		Type productListType = new TypeToken<List<ProductDTO>>() {}.getType();
//		List<ProductDTO> listItemCart = gsonBuilder.setPrettyPrinting()
//				.registerTypeAdapter(LocalDate.class, new LocalDateDeserializer()).create()
//				.fromJson(decodeCookieProduct, productListType);
//		model.addAttribute("listItemCart", listItemCart);
//
//		return "cart-page";
//	}

	
	@PostMapping("/cart")
	public String payment(
			@ModelAttribute("order") @Valid OrderRequest orderDTO,
			@RequestParam(name = "quantityItem") List<Integer> quantityItem, Model model, HttpSession session,
			@CookieValue(value = "cookieProduct",
			defaultValue = "defaultCookieValue") String cookieProduct) {
		
		String decodeCookieProduct = new String(Base64.getDecoder().decode(cookieProduct.getBytes()));
		GsonBuilder gsonBuilder = new GsonBuilder();
		Type productListType = new TypeToken<List<Products>>() {}.getType();
		List<Products> listItemCart = gsonBuilder.setPrettyPrinting()
				.registerTypeAdapter(LocalDate.class, new LocalDateDeserializer()).create()
				.fromJson(decodeCookieProduct, productListType);
		model.addAttribute("listItemCart", listItemCart);
		
		Items items = new Items();
		for (int i = 0; i < listItemCart.size(); i++) {
			items.setProduct(listItemCart.get(i));
			items.setQuantity(quantityItem.get(i));
		}
		
//		int num = quantityItem;
		
		

//    	ResponseEntity<ResponseTransfer> response = restTemplate.postForEntity(url, orderDTO, ResponseTransfer.class);
    	
//    	String urlOrder = orderApi + "{id}";
//		ResponseEntity<ResponseTransfer> response = restTemplate.postForEntity(urlOrder, orderDTO, ResponseTransfer.class);
//		ResponseTransfer newOrder = response.getBody();
    	
    	
		return "cart-page";
	}

}
