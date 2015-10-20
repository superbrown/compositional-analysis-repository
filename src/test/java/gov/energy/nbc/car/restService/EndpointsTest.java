package gov.energy.nbc.car.restService;

import gov.energy.nbc.car.ResearchDataRepositoryApplication;
import gov.energy.nbc.car.TestUsingTestData;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ResearchDataRepositoryApplication.class)
@WebAppConfiguration
public class EndpointsTest extends TestUsingTestData {

	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(),
			Charset.forName("utf8"));

	private MockMvc mockMvc;
	private HttpMessageConverter mappingJackson2HttpMessageConverter;

	@Autowired
	private WebApplicationContext webApplicationContext;


	@BeforeClass
	public static void beforeClass() {

		TestUsingTestData.beforeClass();
	}

	@AfterClass
	public static void afterClass() {
		TestUsingTestData.afterClass();
	}

	@Before
	public void before() {
		super.before();
		this.mockMvc = webAppContextSetup(webApplicationContext).build();

		setServiceEndpointsToUseATestDatabase();
	}

	@After
	public void after() {
		super.after();
	}

	@Test
	public void contextLoads() {
	}


	private void setServiceEndpointsToUseATestDatabase() {
	}

	@Test
	public void testSeedTestData() throws Exception {

//		String url = "/getSpreadsheet/" + TestData.objectId_1 + "?inTestMode=true";

		String url = "/api/seedTestData";

		MockHttpServletRequestBuilder requestBuilder = get(url);

		mockMvc.perform(requestBuilder)
				.andExpect(status().isOk());

//				.andExpect(content().contentType(contentType));

//				.andExpect(jsonPath("spreadsheet", is(this.bookmarkList.get(0).getId().intValue())))
//				.andExpect(jsonPath("$.id", is(this.bookmarkList.get(0).getId().intValue())))
//				.andExpect(jsonPath("$.uri", is("http://bookmark.com/1/" + userName)))
//				.andExpect(jsonPath("$.description", is("A description")));
	}
}
