package g6.tp.despensa.services;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import g6.tp.despensa.entities.Client;
import g6.tp.despensa.entities.Product;
import g6.tp.despensa.entities.Sale;
import g6.tp.despensa.repositories.SaleRepository;

@Service
public class SaleService {

	@Autowired
	private SaleRepository saleRepository;

	@Autowired
	private ProductService productService;

	public Optional<Sale> getSaleById(int id) {
		return this.saleRepository.findById(id);
	}

	@Transactional
	public boolean addSale(Sale s) {
		Map<Integer, Integer> countMap = new HashMap<>();
		for (Product p : s.getProducts()) {
			if (countMap.containsKey(p.getId())) {
				countMap.put(p.getId(), countMap.get(p.getId()) + 1);
			} else {
				countMap.put(p.getId(), 1);
			}
		}
		for (Integer key : countMap.keySet()) {
			if (countMap.get(key) > 3) {
				return false;
			}
		}
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
		this.addSale(sDb);
	}

	public Map<String, String> getMostSold() {
		Map<String, String> responseMap = new HashMap<>();
		Map<Integer, Integer> countMap = new HashMap<>();
		for (Sale s : this.getSales()) {
			for (Product p : s.getProducts()) {
				if (countMap.containsKey(p.getId())) {
					countMap.put(p.getId(), countMap.get(p.getId()) + 1);
				} else {
					countMap.put(p.getId(), 1);
				}
			}
		}
		int maxKey = this.getMaxKey(countMap);
		responseMap.put("product_name", this.productService.getProductById(maxKey).get().getName());
		responseMap.put("total", String.valueOf(countMap.get(maxKey)));
		return responseMap;
	}

	private int getMaxKey(Map<Integer, Integer> m) {
		int maxKey = 0;
		int maxValue = 0;
		for (int i : m.keySet()) {
			if (m.get(i) > maxValue) {
				maxKey = i;
				maxValue = m.get(i);
			}
		}
		return maxKey;
	}

	public List<Map<String, String>> getClientReport(List<Client> c) {
		List<Map<String, String>> responseList = new ArrayList<>();
		for (Client client : c) {
			Set<Sale> clientSales = this.getSalesByClient(client);
			if (clientSales.isEmpty()) {
				continue;
			}
			Map<String, String> responseMap = new HashMap<String, String>();
			double d = 0;
			for (Sale sale : clientSales) {
				responseMap.put("name", sale.getClient().getName());
				for (Product p : sale.getProducts()) {
					d += p.getPrice();
				}
			}
			responseMap.put("total", String.valueOf(d));
			responseList.add(responseMap);
		}
		return responseList;
	}

	public Map<Date, Set<Sale>> getDailyReport() {
		List<Date> dates = new ArrayList<>();
		Map<Date, Set<Sale>> responseMap = new HashMap<>();
		for (Sale s : this.getSales()) {
			if (!dates.contains(s.getDate())) {
				dates.add(s.getDate());
			}
		}
		for (Date d : dates) {
			responseMap.put(d, this.getSalesFrom(d));
		}
		return responseMap;
	}

}
