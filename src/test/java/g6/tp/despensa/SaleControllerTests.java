package g6.tp.despensa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import g6.tp.despensa.entities.Client;
import g6.tp.despensa.entities.Product;
import g6.tp.despensa.entities.Sale;
import g6.tp.despensa.model.ClientReportItem;
import g6.tp.despensa.model.MostSold;
import g6.tp.despensa.services.ClientService;
import g6.tp.despensa.services.SaleService;

@SpringBootTest
@AutoConfigureMockMvc
public class SaleControllerTests {

	@MockBean
	private ClientService clientService;

	@MockBean
	private SaleService saleService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void testGetClientReportListReturns200IfThereAreClients() throws Exception {
		final String testName = "test";
		final double testTotal = 25;

		List<Client> clients = new ArrayList<>();
		Client testClient = new Client();
		testClient.setNombre(testName);
		clients.add(testClient);

		Mockito.when(clientService.getClients()).thenReturn(clients);

		List<ClientReportItem> testClientReport = new ArrayList<>();
		ClientReportItem testClientReportItem = new ClientReportItem();
		testClientReportItem.setClientName(testName);
		testClientReportItem.setTotal(testTotal);
		testClientReport.add(testClientReportItem);

		Mockito.when(saleService.getClientReport(clients)).thenReturn(testClientReport);

		final String expectedContents = "[{\"clientName\":\"test\",\"total\":25.0}]";

		mockMvc.perform(get("/sale/clientReport")) //
				.andExpect(status().isOk()) //
				.andExpect(content().string(expectedContents));
	}

	@Test
	public void testGetDailyReportReturns200WhenHasSales() throws Exception {
		final Date testDate1 = new Date(1636945200000L /* 15/11/2021 */);
		final Date testDate2 = new Date(1636858800000L /* 14/11/2021 */);
		final double testPrice1 = 20;
		final double testPrice2 = 25;
		final String testName1 = "test1";
		final String testName2 = "test2";
		final Client testClient1 = new Client(testName1);
		final Client testClient2 = new Client(testName2);
		final Product testProduct1 = new Product(testName1, testPrice1);
		final Product testProduct2 = new Product(testName2, testPrice2);
		final Sale testSale1 = new Sale(testClient1, List.of(testProduct1, testProduct2), testDate1);
		testSale1.setID(1);
		final Sale testSale2 = new Sale(testClient2, List.of(testProduct1), testDate2);
		testSale2.setID(2);
		final Sale testSale3 = new Sale(testClient2, List.of(testProduct2), testDate2);
		testSale3.setID(3);

		final Map<Date, Set<Sale>> testDailyReport = new HashMap<>();
		final Set<Sale> testSaleSet1 = Set.of(testSale1);
		final Set<Sale> testSaleSet2 = Set.of(testSale2, testSale3);
		testDailyReport.put(testDate1, testSaleSet1);
		testDailyReport.put(testDate2, testSaleSet2);

		Mockito.when(saleService.getDailyReport()).thenReturn(testDailyReport);

		String response = mockMvc.perform(get("/sale/dailyReport")) //
				.andExpect(status().isOk()) //
				.andReturn().getResponse().getContentAsString(); //

		Map<java.util.Date, Set<Sale>> actualSales = objectMapper.readValue(response,
				new TypeReference<Map<java.util.Date, Set<Sale>>>() {
				});
		actualSales.get(testDate1);
		assertThat(actualSales.get(testDate1)).isEqualTo(testSaleSet1);
		assertThat(actualSales.get(testDate2)).isEqualTo(testSaleSet2);
	}

	@Test
	public void testGetMostSoldReturns200() throws Exception {
		final String testName1 = "test";
		final int testQuantity = 5;
		final MostSold testMostSold = new MostSold();
		testMostSold.setProductName(testName1);
		testMostSold.setQuantity(testQuantity);

		Mockito.when(saleService.getMostSold()).thenReturn(testMostSold);

		final String expectedContents = "{\"productName\":\"test\",\"quantity\":5}";

		mockMvc.perform(get("/sale/mostSold")) //
				.andExpect(status().isOk()) //
				.andExpect(content().string(expectedContents));
	}

}
