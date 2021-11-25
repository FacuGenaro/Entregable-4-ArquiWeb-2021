package g6.tp.despensa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import g6.tp.despensa.entities.Client;

//El repositorio contiene la logica necesaria para traer la info desde la base de datos

public interface ClientRepository extends JpaRepository<Client, Integer> {

}
