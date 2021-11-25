package g6.tp.despensa.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import g6.tp.despensa.entities.Product;
import g6.tp.despensa.repositories.ProductRepository;

//Este servicio es llamado mediante el controlador y su función es comunicarse con el Repositorio para hacer los movimientos
//Solicitados en la base de datos


@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;

	//Este metodo recibe como parametro un id e invoca al metodo FindById del Repositorio para devolver el producto solicitado
	public Optional<Product> getProductById(int id) {
		return this.productRepository.findById(id);
	}

	//Este metodo recibe como parametro un producto e invoca al metodo save del Repositorio para agregarlo a la base de datos
	@Transactional
	public boolean addProduct(Product p) {
		this.productRepository.save(p);
		return true;
	}

	//Este metodo llama al findAll del repositorio para traer todos los productos de la base de datos
	public List<Product> getProducts() {
		return this.productRepository.findAll();
	}

	//Este metodo recibe un id como parametro y llama al metodo DeleteById del repositorio para eliminarlo de la base de datos
	@Transactional
	public boolean deleteById(int id) {
		this.productRepository.deleteById(id);
		return true;
	}

	//Este metodo recibe un producto y la nueva info para actualizarlo en la base de datos mediante el metodo Save del repositorio
	public void updateProduct(Product product, Product p) {
		product.setName(p.getName());
		product.setPrice(p.getPrice());
		this.productRepository.save(product);
	}

}
