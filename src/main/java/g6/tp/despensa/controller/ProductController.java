package g6.tp.despensa.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import g6.tp.despensa.entities.Product;
import g6.tp.despensa.services.ProductService;

@RestController
@RequestMapping("/product")
public class ProductController {
	private static Logger LOG = LoggerFactory.getLogger(ProductController.class);

	@Autowired
	private ProductService productService;

	@GetMapping("")
	public List<Product> getAll() {
		return this.productService.getProducts();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Product> getProduct(@PathVariable("id") int id) {
		LOG.info("Buscando producto {}", id);
		Optional<Product> product = this.productService.getProductById(id);
		if (!product.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(product.get(), HttpStatus.OK);
	}

	@PostMapping("")
	public ResponseEntity<Product> addProduct(@RequestBody Product p) {
		boolean ok = this.productService.addProduct(p);
		if (!ok) {
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}
		return new ResponseEntity<Product>(p, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Product> delete(@PathVariable("id") int id) {
		boolean ok = this.productService.deleteById(id);
		if (!ok) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PutMapping("")
	public ResponseEntity<Product> updateProduct(@RequestBody Product p){
		Optional<Product> productDb = this.productService.getProductById(p.getId());
		if (!productDb.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		this.productService.updateProduct(productDb.get(), p);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
