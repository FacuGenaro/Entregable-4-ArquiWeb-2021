package g6.tp.despensa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import g6.tp.despensa.entities.Client;

public interface ClientRepository extends JpaRepository<Client, Integer> {


}
