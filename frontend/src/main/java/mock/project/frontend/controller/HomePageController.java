package mock.project.frontend.controller;

import java.math.BigDecimal;
import java.net.http.HttpRequest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import mock.project.frontend.request.CategoryDTO;
import mock.project.frontend.request.ProductDTO;

@Controller
public class HomePageController {
	private Logger logger = Logger.getLogger(HomePageController.class);
	@Autowired
	private RestTemplate restTemplate;

	@Value("${product.api.url}")
	private String productApi;
	
	@ModelAttribute("listCategories")
	public CategoryDTO[] getCategory() {
		String url2 = productApi + "/categories";
		ResponseEntity<CategoryDTO[]> responseCategories= restTemplate.getForEntity(url2, CategoryDTO[].class);
		CategoryDTO[] listCategories = responseCategories.getBody();
		return listCategories;
	}

	// get list product, and list product date DESC for home-page
	@GetMapping("/")
	public String homePage(Model model,  HttpSession session) {
		String url = productApi + "/products";
		String url1 = productApi + "/desc";
		String url2 = productApi + "/categories";
		ResponseEntity<ProductDTO[]> response = restTemplate.getForEntity(url, ProductDTO[].class);
		ProductDTO[] listProducts = response.getBody();
		ResponseEntity<ProductDTO[]> responseDESC = restTemplate.getForEntity(url1, ProductDTO[].class);
		ProductDTO[] listProductDESC = responseDESC.getBody();
		ResponseEntity<CategoryDTO[]> responseCategories= restTemplate.getForEntity(url2, CategoryDTO[].class);
		CategoryDTO[] listCategories = responseCategories.getBody();

		model.addAttribute("listProductDESCs1", limitList(listProductDESC, 0, 5));
		model.addAttribute("listProductDESCs2", limitList(listProductDESC, 5, 5));
		model.addAttribute("listProductDESCs3", limitList(listProductDESC, 10, 5));

		model.addAttribute("listProducts1", limitList(listProducts, 0, 5));
		model.addAttribute("listProducts2", limitList(listProducts, 5, 5));
		model.addAttribute("listProducts3", limitList(listProducts, 10, 5));
		
		model.addAttribute("listCategories", listCategories);
		
		return "home-page";
	}

	public List<ProductDTO> limitList(ProductDTO[] listProducts, int itemStartIndex, int numberOfItem) {
		List<ProductDTO> listNew = new ArrayList<>();
		for (int i = itemStartIndex; i < (itemStartIndex + numberOfItem); i++) {
			listNew.add(listProducts[i]);
		}
		return listNew;
	}

	// get more product
	@GetMapping("/product")
	public String getMoreProduct(@RequestParam(name = "text", required = false) String text, Model model) {
		if (text.contains("desc")) {
			String url = productApi + "/desc";
			ResponseEntity<ProductDTO[]> responseDESC = restTemplate.getForEntity(url, ProductDTO[].class);
			ProductDTO[] listProductDESC = responseDESC.getBody();
			model.addAttribute("listProducts", listProductDESC);
			model.addAttribute("category", "Sản phẩm mới");
			return "collection-page";
		}
		ResponseEntity<ProductDTO[]> response = restTemplate.getForEntity(productApi + "/products", ProductDTO[].class);
		ProductDTO[] listProducts = response.getBody();
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("category", "Sản phẩm bán chạy");
		return "collection-page";
	}

	@GetMapping("/check-order")
	public String checkOrder() {
		return "check-order-page";
	}
	
	@GetMapping("/order/details")
	public String viewOrderDetails() {
		return "order-view-page";
	}
}
