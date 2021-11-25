package g6.tp.despensa.controller;

import java.util.List;
import java.util.Optional;

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
import g6.tp.despensa.services.ClientService;
import io.swagger.annotations.ApiOperation;

//Esta clase se encarga de setear los endpoints para los clientes y de hacer las llamadas al servicio cuando sea correspondiente

@RestController
@RequestMapping("/client")
public class ClientController {
	private static Logger LOG = LoggerFactory.getLogger(ClientController.class);

	@Autowired
	private ClientService clientService;

	//Este metodo devuelve todos los clientes de la base de datos
	@GetMapping("")
	@ApiOperation(value = "Get all clients", response = Product.class)
	public List<Client> getAll() {
		return this.clientService.getClients();
	}

	//Dado un ID de cliente, este metodo devuelve toda la info de dicho cliente
	@GetMapping("/{id}")
	@ApiOperation(value = "Get client by Id", response = Product.class)
	public ResponseEntity<Client> getClient(@PathVariable("id") int id) {
		LOG.info("Buscando cliente {}", id);
		Optional<Client> client = this.clientService.getClientById(id);
		if (!client.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(client.get(), HttpStatus.OK);
	}

	//Dado un Json con la info de un nuevo cliente, este metodo se comunica con el servicio para insertarlo en la base de datos
	@PostMapping("")
	@ApiOperation(value = "Add a client", response = Product.class)
	public ResponseEntity<Client> addClient(@RequestBody Client c) {
		boolean ok = this.clientService.addClient(c);
		if (!ok) {
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}
		return new ResponseEntity<Client>(c, HttpStatus.OK);
	}

	//Dado un ID de cliente, este metodo se comunica con el servicio para eliminarlo de la base de datos
	@DeleteMapping("/{id}")
	@ApiOperation(value = "Delete a client", response = Product.class)
	public ResponseEntity<Client> delete(@PathVariable("id") int id) {
		boolean ok = this.clientService.deleteById(id);
		if (!ok) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	//Dado un Json con info para actualizar un cliente, este metodo se comunica con el servicio para actualizar esa información
	@PutMapping("")
	@ApiOperation(value = "Update a client", response = Product.class)
	public ResponseEntity<Client> updateClient(@RequestBody Client c) {
		Optional<Client> clientDb = this.clientService.getClientById(c.getId());
		if (!clientDb.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		this.clientService.updateClient(clientDb.get(), c);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
