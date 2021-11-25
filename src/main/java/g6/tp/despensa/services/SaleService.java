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

//Este servicio es llamado mediante el controlador y su función es comunicarse con el Repositorio para hacer los movimientos
//Solicitados en la base de datos

@Service
public class SaleService {

	@Autowired
	private SaleRepository saleRepository;

	//Este metodo recibe como parametro un id e invoca al metodo FindById del Repositorio para devolver el producto solicitado
	public Optional<Sale> getSaleById(int id) {
		return this.saleRepository.findById(id);
	}

	//Este metodo recibe como parametro un producto e invoca al metodo save del Repositorio para agregarlo a la base de datos
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

	//Este metodo llama al findAll del repositorio para traer todos los productos de la base de datos
	public List<Sale> getSales() {
		return this.saleRepository.findAll();
	}

	//Este metodo recibe como parametro una fecha y llama al metodo getSalesFrom del repositorio para devolver las ventas de dicha fecha
	public Set<Sale> getSalesFrom(Date date) {
		return this.saleRepository.getSalesFrom(date);
	}

	//Este metodo recibe un cliente como parametro y mediante el metodo getSalesBByclient definido en el repositorio, trae todas
	//las ventas para ese cliente
	public Set<Sale> getSalesByClient(Client c) {
		return this.saleRepository.getSalesByClient(c.getId());
	}

	//Recibe un id y mediante un llamado al repositorio lo elimina
	@Transactional
	public boolean deleteById(int id) {
		this.saleRepository.deleteById(id);
		return true;
	}

	//Recibe la venta actual que está en la Base de datos y la info para agregar. Acomoda los datos para hacer la actualizacion
	//y mediante un llamado al repositorio los mete en la base de datos
	@Transactional
	public boolean updateSale(Sale sDb, Sale s) {
		sDb.setClient(s.getClient());
		sDb.setDate(s.getDate());
		sDb.setProducts(s.getProducts());
		return this.addSale(sDb);
	}

	//Obtiene el producto más vendido en base a un conteo de todos los productos almacenados en todas las ventas de la base de datos
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

	//Devuelve el mayor valor de un mapa
	private Entry<Product, Integer> getMaxEntry(Map<Product, Integer> m) {
		Entry<Product, Integer> maxEntry = null;
		for (Entry<Product, Integer> i : m.entrySet()) {
			if (maxEntry == null || i.getValue() > maxEntry.getValue()) {
				maxEntry = i;
			}
		}
		return maxEntry;
	}

	//Obtiene el reporte de los clientes, recibe una lista de todos los cleintes y en base a dicha lista obtiene todas las compras
	//y la suma de los montos de ese cliente
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

	//Obtiene todas las ventas ordenadas por fecha
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
