package g6.tp.despensa.repositories;

import java.sql.Date;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import g6.tp.despensa.entities.Sale;

public interface SaleRepository extends JpaRepository<Sale, Integer> {

	// Probar BETWEEN para ver si se puede hacer una bÃºsqueda en un rango (de una
	// semana por ejemplo).
	@Query("SELECT s FROM Sale s WHERE s.date=:date")
	Set<Sale> getSalesFrom(Date date);

	@Query("SELECT s FROM Sale s WHERE s.client.id=:id")
	Set<Sale> getSalesByClient(int id);
	
}
