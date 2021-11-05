package g6.tp.despensa.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Client {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String name;

	public Client() {
		super();
	}

	public Client(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setNombre(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Client [id=" + id + ", name=" + name + "]";
	}

}
