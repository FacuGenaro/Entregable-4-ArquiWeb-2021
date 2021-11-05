package g6.tp.despensa.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import g6.tp.despensa.entities.Client;
import g6.tp.despensa.repositories.ClientRepository;

@Service
public class ClientService {

	@Autowired
	private ClientRepository clientRepository;

	public Optional<Client> getClientById(int id) {
		return this.clientRepository.findById(id);
	}

	@Transactional
	public boolean addClient(Client c) {
		this.clientRepository.save(c);
		return true;
	}

	public List<Client> getClients() {
		return this.clientRepository.findAll();
	}

	@Transactional
	public boolean deleteById(int id) {
		this.clientRepository.deleteById(id);
		return true;
	}

	@Transactional
	public void updateClient(Client client, Client c) {
		client.setNombre(c.getName());
		this.clientRepository.save(client);
	}

}
