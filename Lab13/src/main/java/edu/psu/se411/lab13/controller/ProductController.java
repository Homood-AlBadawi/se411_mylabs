package edu.psu.se411.lab13.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.psu.se411.lab13.model.Product;
import edu.psu.se411.lab13.service.ProductService;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping()
	public ResponseEntity<Iterable<Product>> findAll() {
		return ResponseEntity.ok(productService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Product> findById(@PathVariable @Positive Integer id) {
		Optional<Product> op = productService.findById(id);
		if(op.isPresent()) {
			return ResponseEntity.ok(op.get());
		}
		return ResponseEntity.notFound().build();
	}

}
