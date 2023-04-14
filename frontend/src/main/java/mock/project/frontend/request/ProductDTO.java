package mock.project.frontend.request;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import mock.project.frontend.entities.Categories;
import mock.project.frontend.entities.Images;
import mock.project.frontend.entities.Sizes;

public class ProductDTO {
		
	private Integer productId;
	private String productName;
	private double price;
	private String description;
	private String type;
	private String color;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;
	private List<Sizes> sizes;
	private int quantity;
	private String brand;
	private Categories category;
	private List<Images> images;
	
	public Integer getProductId() {
		return productId;
	}


	public void setProductId(Integer productId) {
		this.productId = productId;
	}


	public String getProductName() {
		return productName;
	}


	public void setProductName(String productName) {
		this.productName = productName;
	}


	public double getPrice() {
		return price;
	}


	public void setPrice(double price) {
		this.price = price;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getColor() {
		return color;
	}


	public void setColor(String color) {
		this.color = color;
	}


	public LocalDate getDate() {
		return date;
	}


	public void setDate(LocalDate date) {
		this.date = date;
	}


	public List<Sizes> getSizes() {
		return sizes;
	}


	public void setSizes(List<Sizes> sizes) {
		this.sizes = sizes;
	}


	public int getQuantity() {
		return quantity;
	}


	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}


	public String getBrand() {
		return brand;
	}


	public void setBrand(String brand) {
		this.brand = brand;
	}


	public Categories getCategory() {
		return category;
	}


	public void setCategory(Categories category) {
		this.category = category;
	}


	public List<Images> getImages() {
		return images;
	}


	public void setImages(List<Images> images) {
		this.images = images;
	}


	@Override
	public String toString() {
		return "ProductDTO [productId=" + productId + ", productName=" + productName + ", price=" + price
				+ ", description=" + description + ", type=" + type + ", color=" + color + ", date=" + date + ", sizes="
				+ sizes + ", quantity=" + quantity + ", brand=" + brand + ", category=" + category + ", images="
				+ images + "]";
	}
	
}
