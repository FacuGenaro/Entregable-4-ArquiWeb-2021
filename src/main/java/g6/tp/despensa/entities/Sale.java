package g6.tp.despensa.entities;

import java.sql.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
public class Sale {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private Date date;

	@ManyToOne
	@JoinColumn
	private Client client;

	@ManyToMany
	@JoinColumn
	private List<Product> products;

	public Sale() {
		super();
	}

	public Sale(Client client, List<Product> products, Date date) {
		super();
		this.client = client;
		this.products = products;
		this.date = date;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getId() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof Sale)) {
			return false;
		}

		Sale oSale = (Sale) o;
		return oSale.id == this.id;
	}

	@Override
	public int hashCode() {
		return Integer.hashCode(id);
	}
}
