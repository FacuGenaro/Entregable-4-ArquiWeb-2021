package g6.tp.despensa.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import g6.tp.despensa.entities.Client;
import g6.tp.despensa.repositories.ClientRepository;

//Este servicio es llamado mediante el controlador y su función es comunicarse con el Repositorio para hacer los movimientos
//Solicitados en la base de datos

@Service
public class ClientService {

	@Autowired
	private ClientRepository clientRepository;

	//Este metodo recibe como parametro un id e invoca al metodo FindById del Repositorio para devolver el cliente solicitado
	public Optional<Client> getClientById(int id) {
		return this.clientRepository.findById(id);
	}

	@Transactional
	//Este metodo recibe como parametro un cliente e invoca al metodo save del Repositorio para agregar el cliente a la base de datos
	public boolean addClient(Client c) {
		this.clientRepository.save(c);
		return true;
	}

	//Este metodo llama al findAll del repositorio para traer todos los clientes de la base de datos
	public List<Client> getClients() {
		return this.clientRepository.findAll();
	}

	//Este metodo recibe un id como parametro y llama al metodo DeleteById del repositorio para eliminarlo de la base de datos
	@Transactional
	public boolean deleteById(int id) {
		this.clientRepository.deleteById(id);
		return true;
	}

	//Este metodo recibe un cliente y la nueva info para actualizarlo en la base de datos mediante el metodo Save del repositorio
	@Transactional
	public void updateClient(Client client, Client c) {
		client.setNombre(c.getName());
		this.clientRepository.save(client);
	}

}
