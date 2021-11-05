package g6.tp.despensa.services;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import g6.tp.despensa.entities.Client;
import g6.tp.despensa.entities.Sale;
import g6.tp.despensa.repositories.SaleRepository;

@Service
public class SaleService {

	@Autowired
	private SaleRepository saleRepository;

	public Optional<Sale> getSaleById(int id) {
		return this.saleRepository.findById(id);
	}

	@Transactional
	public boolean addSale(Sale s) {
		this.saleRepository.save(s);
		return true;
	}

	public List<Sale> getSales() {
		return this.saleRepository.findAll();
	}

	public Set<Sale> getSalesFrom(Date date) {
		return this.saleRepository.getSalesFrom(date);
	}

	public Set<Sale> getSalesByClient(Client c) {
		return this.saleRepository.getSalesByClient(c.getId());
	}

	@Transactional
	public boolean deleteById(int id) {
		this.saleRepository.deleteById(id);
		return true;
	}

	@Transactional
	public void updateSale(Sale sDb, Sale s) {
		sDb.setClient(s.getClient());
		sDb.setDate(s.getDate());
		sDb.setProducts(s.getProducts());
		this.saleRepository.save(sDb);		
	}

}
