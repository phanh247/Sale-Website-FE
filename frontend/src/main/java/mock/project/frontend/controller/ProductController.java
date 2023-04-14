package mock.project.frontend.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mock.project.frontend.LocalDateSerializer;
import mock.project.frontend.entities.Categories;
import mock.project.frontend.entities.Sizes;
import mock.project.frontend.request.CategoryDTO;
import mock.project.frontend.request.ProductDTO;
import mock.project.frontend.request.ProductRequest;
import mock.project.frontend.request.SizeDTO;

@Controller
public class ProductController {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${product.api.url}")
	private String productApi;
	
	List<ProductDTO> dtos = new ArrayList<>();
	
	@ModelAttribute("listCategories")
	public CategoryDTO[] getCategory() {
		String url2 = productApi + "/categories";
		ResponseEntity<CategoryDTO[]> responseCategories= restTemplate.getForEntity(url2, CategoryDTO[].class);
		CategoryDTO[] listCategories = responseCategories.getBody();
		return listCategories;
	}
	
	//search product
	@GetMapping("/search")
	public String listProductBySearch(@RequestParam(name="searchField",required = false)String searchField,Model model) {
		if(searchField == null) {
			ResponseEntity<ProductDTO[]> response = restTemplate.getForEntity(productApi, ProductDTO[].class);
			ProductDTO[] listProducts = response.getBody();
			model.addAttribute("listProducts", listProducts);
			model.addAttribute("category", "Search");
			return "collection-page";
			}
		String url = productApi+"/search?searchField="+searchField;
		ResponseEntity<ProductDTO[]> response = restTemplate.getForEntity(url, ProductDTO[].class);
		ProductDTO[] listProducts = response.getBody();
		if (listProducts.length == 0) {
			model.addAttribute("msg","Have no items matches ");
			model.addAttribute("category", "Search");
			return "collection-page";
		}
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("category", "Search");
		return "collection-page";
	}

	//get list product by brand
	@GetMapping("/category/{id}")
	public String getProductByCategory(@PathVariable(name = "id", required = false) Integer id,
			@RequestParam(name = "type", required = false) String type, Model model) {
		String url = productApi + "/category/" + id;
		String url1 = productApi + "/category/" + id + "?type=" + type;
		String search = null;
		ProductDTO[] listProducts;
		if (type == null || type == "") {
			ResponseEntity<ProductDTO[]> response = restTemplate.getForEntity(url, ProductDTO[].class);
			listProducts = response.getBody();
		} else {
			ResponseEntity<ProductDTO[]> response = restTemplate.getForEntity(url1, ProductDTO[].class);
			listProducts = response.getBody();
		}
		if (listProducts.length == 0) {
			model.addAttribute("msg","Have no items matches ");
			model.addAttribute("category", "Search");
			return "collection-page";
		}
		if (listProducts != null) {
			String categoryName = listProducts[0].getCategory().getCategoryName();
			Integer categoryId = listProducts[0].getCategory().getCategoryId();
			model.addAttribute("listProducts", listProducts);
			model.addAttribute("category", categoryName);
			model.addAttribute("categoryId", categoryId);
			return "collection-page";
		}

		model.addAttribute("listProducts", listProducts);
		model.addAttribute("category", search);
		return "collection-page";
	}
	
	// get list product by type
	@GetMapping("/gender")
	public String getProductByType(@RequestParam(name = "type", required = false) String type, Model model) {
		String url = productApi + "/gender" + "?type=" + type;
		ProductDTO[] listProducts;
		ResponseEntity<ProductDTO[]> response = restTemplate.getForEntity(url, ProductDTO[].class);
		listProducts = response.getBody();
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("category", type);
		return "collection-page";
	}
	//get product detail
	@GetMapping("/product/{id}")
	public String getProductDetails(@PathVariable(name = "id", required = false) Integer id, Model model) {
		String url = productApi + "/" + id;
		ProductDTO product = restTemplate.getForObject(url, ProductDTO.class);
		model.addAttribute("product", product);
		String urlProduct = productApi + "/products" ;
		ResponseEntity<ProductDTO[]> response = restTemplate.getForEntity(urlProduct, ProductDTO[].class);
		ProductDTO[] listProducts = response.getBody();
		model.addAttribute("listProducts1", limitList(listProducts, 0, 5));
		model.addAttribute("listProducts2", limitList(listProducts, 5, 5));
		model.addAttribute("listProducts3", limitList(listProducts, 10, 5));
		model.addAttribute("listProducts4", limitList(listProducts, 6, 3));
		return "product-detail-page";
	}
	
	public List<ProductDTO> limitList(ProductDTO[] listProducts, int itemStartIndex, int numberOfItem) {
		List<ProductDTO> listNew = new ArrayList<>();
		for (int i = itemStartIndex ; i<(itemStartIndex + numberOfItem); i++) {
			 listNew.add(listProducts[i]);
		}
		return listNew;
	}
	
	//sort by price
	@GetMapping("/search/sort")
	public String sortProductByPrice(@RequestParam(name="sort",required = false)String sort,Model model) {
		
		String url = productApi+"/price?sort="+ sort;
		ResponseEntity<ProductDTO[]> response = restTemplate.getForEntity(url, ProductDTO[].class);
		ProductDTO[] listProducts = response.getBody();
		model.addAttribute("listProducts", listProducts);
		return "collection-page";
	}
	
	@PostMapping("/product/{id}")
	public String getSize(Model model, @PathVariable(name = "id", required = false) Integer id,
			@RequestParam(name = "sizes") String size, HttpServletRequest request, HttpServletResponse response) {
		String url = productApi + "/" + id;
		ProductDTO product = restTemplate.getForObject(url, ProductDTO.class);
		model.addAttribute("product", product);
		
		Sizes sizeSet = new Sizes(stringToInt(size));
		List<Sizes> sizes = new ArrayList<>();
		sizes.add(sizeSet);
		product.setSizes(sizes);
		
		dtos.add(product);
		String jsonProduct = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(LocalDate.class, new LocalDateSerializer()).create().toJson(dtos);
		String encode = Base64.getEncoder().encodeToString(jsonProduct.getBytes());
		Cookie cookie = new Cookie("cookieProduct", encode);
		cookie.setMaxAge(24 * 60 * 60);
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		cookie.setPath("/"); // global cookie accessible every where
		response.addCookie(cookie);

		return "product-detail-page";
	}

	//search product filter
	@PostMapping("/search/filter")
	public String filterProduct(@ModelAttribute("productRq") ProductRequest productRequest, Model model) {
		String url = productApi+ "/filter";
		int value = (int) productRequest.getStartRangePrice();
		switch(value) {
		  case 12:
			  productRequest.setStartRangePrice(1000000);
			  productRequest.setEndRangePrice(2000000);
		    break;
		  case 23:
			  productRequest.setStartRangePrice(2000000);
				productRequest.setEndRangePrice(3000000);
		    break;
		  case 34:
			  productRequest.setStartRangePrice(3000000);
				productRequest.setEndRangePrice(4000000);
			    break;
		  case 45:
			  productRequest.setStartRangePrice(4000000);
				productRequest.setEndRangePrice(5000000);
			    break;
		  case 56:
			  productRequest.setStartRangePrice(5000000);
				productRequest.setEndRangePrice(6000000);
			    break;
		  case 610:
			  productRequest.setStartRangePrice(6000000);
				productRequest.setEndRangePrice(10000000);
			    break;
		  default:
			  productRequest.setStartRangePrice(1000000);
				productRequest.setEndRangePrice(10000000);
		}
		ProductDTO[] listProducts = restTemplate.postForObject(url, productRequest,ProductDTO[].class);
		if (listProducts.length == 0) {
			model.addAttribute("msg","Have no items matches ");
			model.addAttribute("category", "Filter");
			return "collection-page";
		}
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("category", "Filter");
		return "collection-page";
	}
	
	public int stringToInt(String sizeString) {
		int sizeInt = Integer.parseInt(sizeString);  
		return sizeInt;
	}
	@GetMapping("/coming")
	public String commingSoonView(Model model) {
		model.addAttribute("error", "Coming Soon");
		model.addAttribute("status", "Hello");
		return "error";
	}
}
