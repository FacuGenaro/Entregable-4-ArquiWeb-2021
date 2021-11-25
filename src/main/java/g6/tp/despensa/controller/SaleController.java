package g6.tp.despensa.controller;

import java.sql.Date;
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
import g6.tp.despensa.model.ClientReportItem;
import g6.tp.despensa.model.MostSold;
import g6.tp.despensa.services.ClientService;
import g6.tp.despensa.services.SaleService;
import io.swagger.annotations.ApiOperation;

//Esta clase se encarga de setear los endpoints para las ventas y de hacer las llamadas al servicio cuando sea correspondiente

@RestController
@RequestMapping("/sale")
public class SaleController {
	private static Logger LOG = LoggerFactory.getLogger(SaleController.class);

	@Autowired
	private SaleService saleService;

	@Autowired
	private ClientService clientService;

	//Este metodo trae todas las ventas que haya en la base de datos
	@GetMapping("")
	@ApiOperation(value = "Get all sales", response = List.class)
	public List<Sale> getAll() {
		return this.saleService.getSales();
	}

	//Dado un ID este metodo trae las ventas relacionadas a dicho ID
	@GetMapping("/{id}")
	@ApiOperation(value = "Get sale by id", response = Product.class)
	public ResponseEntity<Sale> getSale(@PathVariable("id") int id) {
		LOG.info("Buscando venta {}", id);
		Optional<Sale> sale = this.saleService.getSaleById(id);
		if (!sale.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(sale.get(), HttpStatus.OK);
	}

	//Este método recibe un Json con los datos de una nueva venta y lo agrega a la base de datos mediante el Servicio
	@PostMapping("")
	@ApiOperation(value = "Add a sale", response = Product.class)
	public ResponseEntity<Sale> addSale(@RequestBody Sale s) {
		boolean ok = this.saleService.addSale(s);
		if (!ok) {
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}
		return new ResponseEntity<Sale>(s, HttpStatus.OK);
	}

	//Este metodo recibe un ID y elimina la venta que posea dicho ID
	@DeleteMapping("/{id}")
	@ApiOperation(value = "Delete a sale by id", response = Product.class)
	public ResponseEntity<Sale> delete(@PathVariable("id") int id) {
		boolean ok = this.saleService.deleteById(id);
		if (!ok) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	// Este metodo recibe un Json y en base al ID que traiga dicho Json se encarga de actualizar la información en la base de datos
	@PutMapping("")
	@ApiOperation(value = "Update a sale", response = Product.class)
	public ResponseEntity<Sale> updateSale(@RequestBody Sale s) {
		Optional<Sale> saleDb = this.saleService.getSaleById(s.getId());
		if (!saleDb.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		if (this.saleService.updateSale(saleDb.get(), s)) {
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
	}

	//Inciso 3 - Este metodo devuelve todos los clientes con el monto total que gastaron
	@GetMapping("/clientReport")
	@ApiOperation(value = "Get client report", response = Product.class)
	public ResponseEntity<List<ClientReportItem>> getClientReport() {
		List<Client> c = clientService.getClients();
		if (c.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		List<ClientReportItem> responseList = this.saleService.getClientReport(c);
		return new ResponseEntity<>(responseList, HttpStatus.OK);
	}

	//Inciso 4 - Este metodo trae todas las compras que se hicieron en cada día almacenado en la base de datos
	@GetMapping("/dailyReport")
	@ApiOperation(value = "Get daily report", response = Product.class)
	public ResponseEntity<Map<Date, Set<Sale>>> getDailyReport() {
		Map<Date, Set<Sale>> responseMap = this.saleService.getDailyReport();
		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}

	//Inciso 5 - Este metodo trae el producto más vendido
	@GetMapping("/mostSold")
	@ApiOperation(value = "Get most sold", response = Product.class)
	public ResponseEntity<MostSold> getMostSold() {
		MostSold mostSold = saleService.getMostSold();
		return new ResponseEntity<>(mostSold, HttpStatus.OK);
	}

}
