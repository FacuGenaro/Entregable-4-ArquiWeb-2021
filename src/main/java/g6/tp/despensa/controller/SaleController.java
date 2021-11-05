package g6.tp.despensa.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

import g6.tp.despensa.entities.Client;
import g6.tp.despensa.entities.Product;
import g6.tp.despensa.entities.Sale;
import g6.tp.despensa.services.ClientService;
import g6.tp.despensa.services.SaleService;

@RestController
@RequestMapping("/sale")
public class SaleController {
	private static Logger LOG = LoggerFactory.getLogger(SaleController.class);

	@Autowired
	private SaleService saleService;

	@Autowired
	private ClientService clientService;

	@GetMapping("")
	public List<Sale> getAll() {
		return this.saleService.getSales();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Sale> getSale(@PathVariable("id") int id) {
		LOG.info("Buscando venta {}", id);
		Optional<Sale> sale = this.saleService.getSaleById(id);
		if (!sale.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(sale.get(), HttpStatus.OK);
	}

	@PostMapping("")
	public ResponseEntity<Sale> addSale(@RequestBody Sale s) {
		boolean ok = this.saleService.addSale(s);
		if (!ok) {
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}
		return new ResponseEntity<Sale>(s, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Sale> delete(@PathVariable("id") int id) {
		boolean ok = this.saleService.deleteById(id);
		if (!ok) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	// Solo toma en cuenta el id del cliente y el producto para actualizar la sale
	@PutMapping("")
	public ResponseEntity<Sale> updateSale(@RequestBody Sale s) {
		Optional<Sale> saleDb = this.saleService.getSaleById(s.getId());
		if (!saleDb.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		this.saleService.updateSale(saleDb.get(), s);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	// 3) Genere un reporte donde se indiquen los clientes y el monto total de sus
	// compras
	@GetMapping("/informeClientes")
	public ResponseEntity<List<Map<String, String>>> getByClient() {
		List<Client> c = clientService.getClients();
		List<Map<String, String>> responseList = new ArrayList<>();
		if (c.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		for (Client client : c) {
			Set<Sale> clientSales = saleService.getSalesByClient(client);
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
		return new ResponseEntity<>(responseList, HttpStatus.OK);
	}

	// 4) Genere un reporte con las ventas por día
	@GetMapping("/dailyReport")
	public Set<Sale> getDailyReport() {
		return saleService.getSalesFrom(Date.valueOf(LocalDate.now()));
	}

	// 5) Implemente una consulta para saber cúal fue el producto más vendido
	@GetMapping("/mostSold")
	public ResponseEntity<Map<String, String>> getMostSold() {
		List<Sale> sales = saleService.getSales();
		Map<String, String> responseMap = new HashMap<>();
		if (sales.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Map<Integer, Integer> countMap = new HashMap<>();
		for (Sale s : sales) {
			for (Product p : s.getProducts()) {
				if (countMap.containsKey(p.getId())) {
					countMap.put(p.getId(), countMap.get(p.getId()) + 1);
				} else {
					countMap.put(p.getId(), 1);
				}
			}
		}
		int maxKey = this.getMaxKey(countMap);
		responseMap.put("Id_product", String.valueOf(maxKey));
		responseMap.put("total", String.valueOf(countMap.get(maxKey)));
		return new ResponseEntity<>(responseMap, HttpStatus.OK);
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

	@GetMapping("/byClient/{client_id}")
	public ResponseEntity<Map<String, String>> getByClient(@PathVariable("client_id") int client_id) {
		Optional<Client> c = clientService.getClientById(client_id);
		Map<String, String> responseMap = new HashMap<>();
		if (c.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Set<Sale> clientSales = saleService.getSalesByClient(c.get());
		double d = 0;
		for (Sale sale : clientSales) {
			responseMap.put("name", sale.getClient().getName());
			for (Product p : sale.getProducts()) {
				d += p.getPrice();
			}
		}
		responseMap.put("total", String.valueOf(d));
		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}
}
