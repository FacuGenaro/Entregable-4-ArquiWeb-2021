package g6.tp.despensa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import g6.tp.despensa.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}
