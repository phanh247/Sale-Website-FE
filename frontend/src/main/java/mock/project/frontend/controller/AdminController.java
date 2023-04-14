package mock.project.frontend.controller;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.hibernate.internal.build.AllowSysOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import mock.project.frontend.entities.Images;
import mock.project.frontend.entities.Sizes;
import mock.project.frontend.request.CategoryDTO;
import mock.project.frontend.request.OrderDTO;
import mock.project.frontend.request.ProductDTO;
import mock.project.frontend.request.SizeDTO;
import mock.project.frontend.request.UserDTO;
import mock.project.frontend.request.UserDTOReponse;
import mock.project.frontend.response.ResponseTransfer;

@Controller
@RequestMapping("/admin")
public class AdminController {
	private Logger logger = Logger.getLogger(AdminController.class);
	@Autowired
	private RestTemplate restTemplate;

	@Value("${product.api.url}")
	private String productApi;

	@Value("${authentication.api.url}")
	private String authenticationApi;

	@Value("${user.api.url}")
	private String userApi;

	@Value("${admin.api.url}")
	private String adminApi;

	static String jwt;
	
	@ModelAttribute("user")
	public UserDTO userInfo(HttpSession session) {
		String username = (String)session.getAttribute("username");
		if(session != null) {
		String url = userApi + "/info?username="+ username;
		UserDTO userInfo = restTemplate.getForObject(url, UserDTO.class);
		return userInfo;
		}
		return new UserDTO();
	}
	
	@ModelAttribute("categories")
	public CategoryDTO[] getCategory() {
		String url2 = productApi + "/categories";
		ResponseEntity<CategoryDTO[]> responseCategories= restTemplate.getForEntity(url2, CategoryDTO[].class);
		CategoryDTO[] listCategories = responseCategories.getBody();
		return listCategories;
	}
	@ModelAttribute("sizes")
	public List<Sizes> getSizes() {
		String url2 = productApi + "/sizes";
		ResponseEntity<Sizes[]> responseCategories= restTemplate.getForEntity(url2, Sizes[].class);
		List<Sizes> listSizes= Arrays.asList(responseCategories.getBody());
		return listSizes;
	}
	
	// welcome admin
	@GetMapping("/dashboard")
	public String adminViews(Model model, HttpServletRequest request) throws UnsupportedEncodingException {
		logger.info("Welcome admin");
		Cookie cookie = null;
		Cookie[] cookies = null;
		String token = null;
		cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				cookie = cookies[i];
				if ((cookie.getName()).equals("Authorization")) {
					token = cookie.getValue();
					break;
				}
			}
		}
		AdminController.jwt = URLDecoder.decode(token, "UTF-8");
//		try {
//			String url = adminApi + "/check";
//			HttpHeaders headers = new HttpHeaders();
//			headers.setContentType(MediaType.APPLICATION_JSON);
//			headers.set("Authorization", jwt);
//			HttpEntity<String> jwtEntity = new HttpEntity<String>(headers);
//			ResponseEntity<String> responseAPI = restTemplate.exchange(url, HttpMethod.GET, jwtEntity, String.class);
//		} catch (Exception e) {
//			model.addAttribute("error", "forbidden");
//			model.addAttribute("status", "403");
//			return "error";
//		}
		return "dashboard";
	}

	// list user
	@GetMapping("/user")
	public String listOfUser(Model model) {
		logger.info("List of all user ");
		String url = adminApi + "/user";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", AdminController.jwt);
		HttpEntity<String> jwtEntity = new HttpEntity<String>(headers);
		ResponseEntity<UserDTO[]> responseAPI = restTemplate.exchange(url, HttpMethod.GET, jwtEntity, UserDTO[].class);
		UserDTO[] listUsers = responseAPI.getBody();
		model.addAttribute("listUsers", listUsers);
		model.addAttribute("numberUsers", listUsers.length);
		return "user-list";
	}

	// list order
	@GetMapping("/order/list")
	public String viewOrderList(Model model) {
		logger.info("List of orders");
		String url = adminApi + "/order";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", AdminController.jwt);
		HttpEntity<String> jwtEntity = new HttpEntity<String>(headers);
		ResponseEntity<OrderDTO[]> responseAPI = restTemplate.exchange(url, HttpMethod.GET, jwtEntity,
				OrderDTO[].class);
		OrderDTO[] listOrders = responseAPI.getBody();
		model.addAttribute("listOrders", listOrders);
		return "order-list";
	}

	@GetMapping("/order")
	public String viewOrderDashboard(Model model, HttpServletRequest request) {
		logger.info("Loading view order dashboard.....");
		try {
			String url = adminApi + "/check";
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", AdminController.jwt);
			HttpEntity<String> jwtEntity = new HttpEntity<String>(headers);
			ResponseEntity<String> responseAPI = restTemplate.exchange(url, HttpMethod.GET, jwtEntity, String.class);
		} catch (Exception e) {
			model.addAttribute("error", "forbidden");
			model.addAttribute("status", "403");
			return "error";
		}
		return "order-dashboard";
	}

	// add new product view
	@GetMapping("/product/add")
	public String viewAddNewProduct(Model model) {
		logger.info("Loading add new product view..");
		model.addAttribute("product", new ProductDTO());
		return "add-product";
	}

	// add new product
	@PostMapping("/product/add")
	public String addNewProduct(@ModelAttribute("product") ProductDTO product,@RequestParam(name="sizeArr",required = false) String[] size, Model model) {
		String x;
		String y=null;
		for(int i=0;i<size.length;i++) {
			x= size[i];
			y=y+","+x;
		}
		System.out.println(product);
		String url = adminApi + "/product?sizes="+ y;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", AdminController.jwt);
		HttpEntity<ProductDTO> jwtEntity = new HttpEntity<ProductDTO>(product,headers);
		ResponseEntity<ProductDTO> responseAPI = restTemplate.exchange(url, HttpMethod.POST, jwtEntity,
				ProductDTO.class);
		ProductDTO productTDO = responseAPI.getBody();
		if (productTDO == null) {
			model.addAttribute("msg", "Something went wrong ");
			return "add-product";
		}
		model.addAttribute("product", responseAPI);
		model.addAttribute("msg", "Add successful");
		return "add-product";
	}

	// update product view
	@GetMapping("/product/update/{id}")
	public String updateProductView(@PathVariable(name = "id", required = false) Integer id, Model model) {
		logger.info("Loading update product view..");
		String url = productApi + "/" + id;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", AdminController.jwt);
		HttpEntity<String> jwtEntity = new HttpEntity<String>(headers);
		ResponseEntity<ProductDTO> responseAPI = restTemplate.exchange(url, HttpMethod.GET, jwtEntity,
				ProductDTO.class);
		ProductDTO product = responseAPI.getBody();
		model.addAttribute("product", product);
		return "edit-product";
	}

	// udpate product
	@PostMapping("/product/update/{id}")
	public String udpateProduct(@PathVariable(value = "id", required = false) Integer id,
			@ModelAttribute("product") ProductDTO product, Model model) {
		String url = adminApi + "/product/" + id;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", AdminController.jwt);
		product.setProductId(id);
		HttpEntity<ProductDTO> jwtEntity = new HttpEntity<ProductDTO>(product, headers);
		ResponseEntity<ProductDTO> response = restTemplate.exchange(url, HttpMethod.PUT, jwtEntity, ProductDTO.class);
		model.addAttribute("product", response.getBody());
		return "edit-product";
	}

	// get product list
	@GetMapping("/product/list")
	public String viewProductList(Model model, HttpServletRequest request) {
		logger.info("viewProductList -> jwt: " + jwt);
		String url = adminApi + "/products";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", AdminController.jwt);
		HttpEntity<String> jwtEntity = new HttpEntity<String>(headers);
		ParameterizedTypeReference<List<ProductDTO>> typeRef = new ParameterizedTypeReference<List<ProductDTO>>() {
		};
		ResponseEntity<List<ProductDTO>> responseAPI = restTemplate.exchange(url, HttpMethod.GET, jwtEntity, typeRef);
		List<ProductDTO> listProducts = responseAPI.getBody();
		model.addAttribute("listProducts", listProducts);
		return "product-list";
	}

	// load view product dashboard
	@GetMapping("/product")
	public String viewProductDashboard(Model model, HttpServletRequest request) {
		logger.info("Loading view product dashboard.....");
		try {
			String url = adminApi + "/check";
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", AdminController.jwt);
			HttpEntity<String> jwtEntity = new HttpEntity<String>(headers);
			ResponseEntity<String> responseAPI = restTemplate.exchange(url, HttpMethod.GET, jwtEntity, String.class);
		} catch (Exception e) {
			model.addAttribute("error", "forbidden");
			model.addAttribute("status", "403");
			return "error";
		}
		return "product-dashboard";
	}

	// delete product
	@GetMapping("/product/delete/{id}")
	public String viewProductList(@PathVariable(name = "id", required = false) Integer id, Model model,
			HttpServletRequest request) {
		String url = adminApi + "/product/" + id;
		String url1 = adminApi + "/products";
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", AdminController.jwt);
			HttpEntity<String> jwtEntity = new HttpEntity<String>(headers);
			ResponseEntity<ProductDTO> responseAPIDelete = restTemplate.exchange(url, HttpMethod.DELETE, jwtEntity,
					ProductDTO.class);
			ParameterizedTypeReference<List<ProductDTO>> typeRef = new ParameterizedTypeReference<List<ProductDTO>>() {
			};
			ResponseEntity<List<ProductDTO>> responseAPI = restTemplate.exchange(url1, HttpMethod.GET, jwtEntity,
					typeRef);
			List<ProductDTO> listProducts = responseAPI.getBody();
			model.addAttribute("listProducts", listProducts);
			model.addAttribute("msg", "Deleted successful!");
		} catch (Exception e) {
			model.addAttribute("error", "forbidden");
			model.addAttribute("status", "403");
			return "error";
		}
		return "product-list";
	}

}
