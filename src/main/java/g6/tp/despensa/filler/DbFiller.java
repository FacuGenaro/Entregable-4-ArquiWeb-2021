package g6.tp.despensa.filler;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalLong;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import g6.tp.despensa.entities.Client;
import g6.tp.despensa.entities.Product;
import g6.tp.despensa.entities.Sale;
import g6.tp.despensa.repositories.ClientRepository;
import g6.tp.despensa.repositories.ProductRepository;
import g6.tp.despensa.repositories.SaleRepository;

@Configuration
public class DbFiller {

	@Bean
	public CommandLineRunner initDb(ClientRepository client, ProductRepository product, SaleRepository sale) {
		return args -> {
			IntStream.range(0, 10).forEach((i) -> {
				Client c = new Client("Cliente " + i);
				Product l = new Product("Producto " + i, i);
				client.save(c);
				product.save(l);
			});
			List<Client> cList = client.findAll();
			List<Product> pList = product.findAll();

			IntStream.range(0, 5).forEach((i) -> {
				Set<Product> pSet = new HashSet<>();

				Random r = new Random();

				Client c = cList.get(r.nextInt(10));
				r.ints(5, 0, 10).forEach((j) -> {
					Product p = pList.get(j);
					pSet.add(p);
				});
				long today = LocalDate.now().toEpochDay();
				long yesterday = today - 1;
				OptionalLong randomDate = r.longs(yesterday, today + 1).findFirst();

				Sale s = new Sale(c, pSet, Date.valueOf(LocalDate.ofEpochDay(randomDate.getAsLong())));
				sale.save(s);
			});
		};
	}
}
