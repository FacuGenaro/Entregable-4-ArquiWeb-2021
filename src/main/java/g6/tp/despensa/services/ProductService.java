package g6.tp.despensa.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import g6.tp.despensa.entities.Product;
import g6.tp.despensa.repositories.ProductRepository;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;

	public Optional<Product> getProductById(int id) {
		return this.productRepository.findById(id);
	}

	@Transactional
	public boolean addProduct(Product p) {
		this.productRepository.save(p);
		return true;
	}

	public List<Product> getProducts() {
		return this.productRepository.findAll();
	}

	@Transactional
	public boolean deleteById(int id) {
		this.productRepository.deleteById(id);
		return true;
	}

	public void updateProduct(Product product, Product p) {
		product.setName(p.getName());
		product.setPrice(p.getPrice());
		this.productRepository.save(product);
	}

}
