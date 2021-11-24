package g6.tp.despensa.services;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import g6.tp.despensa.entities.Client;
import g6.tp.despensa.entities.Product;
import g6.tp.despensa.entities.Sale;
import g6.tp.despensa.model.ClientReportItem;
import g6.tp.despensa.model.MostSold;
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
	public boolean updateSale(Sale sDb, Sale s) {
		sDb.setClient(s.getClient());
		sDb.setDate(s.getDate());
		sDb.setProducts(s.getProducts());
		return this.addSale(sDb);
	}

	public MostSold getMostSold() {
		Map<Product, Integer> countMap = new HashMap<>();
		for (Sale s : this.getSales()) {
			for (Product p : s.getProducts()) {
				countMap.put(p, countMap.getOrDefault(p, 0) + 1);
			}
		}
		Entry<Product, Integer> maxEntry = this.getMaxEntry(countMap);
		MostSold result = new MostSold();
		result.setProductName(maxEntry.getKey().getName());
		result.setQuantity(maxEntry.getValue().intValue());
		return result;
	}

	private Entry<Product, Integer> getMaxEntry(Map<Product, Integer> m) {
		Entry<Product, Integer> maxEntry = null;
		for (Entry<Product, Integer> i : m.entrySet()) {
			if (maxEntry == null || i.getValue() > maxEntry.getValue()) {
				maxEntry = i;
			}
		}
		return maxEntry;
	}

	public List<ClientReportItem> getClientReport(List<Client> c) {
		List<ClientReportItem> responseList = new ArrayList<>();
		for (Client client : c) {
			Set<Sale> clientSales = this.getSalesByClient(client);
			if (clientSales.isEmpty()) {
				continue;
			}
			ClientReportItem clientReportItem = new ClientReportItem();
			double d = 0;
			for (Sale sale : clientSales) {
				clientReportItem.setClientName(sale.getClient().getName());
				for (Product p : sale.getProducts()) {
					d += p.getPrice();
				}
			}
			clientReportItem.setTotal(d);
			responseList.add(clientReportItem);
		}
		return responseList;
	}

	public Map<Date, Set<Sale>> getDailyReport() {
		Set<Date> dates = new HashSet<>();
		Map<Date, Set<Sale>> responseMap = new HashMap<>();
		for (Sale s : this.getSales()) {
			dates.add(s.getDate());
		}
		for (Date d : dates) {
			responseMap.put(d, this.getSalesFrom(d));
		}
		return responseMap;
	}

}
