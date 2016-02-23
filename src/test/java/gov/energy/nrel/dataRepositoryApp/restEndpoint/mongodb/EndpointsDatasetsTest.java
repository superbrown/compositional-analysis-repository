//package gov.energy.nrel.dataRepositoryApp.restEndpoint.mongodb;
//
//import gov.energy.nrel.dataRepositoryApp.Application;
//import gov.energy.nrel.dataRepositoryApp.CARApplication;
//import gov.energy.nrel.dataRepositoryApp.Settings;
//import gov.energy.nrel.dataRepositoryApp.Settings_forUnitTestPurposes;
//import gov.energy.nrel.dataRepositoryApp.bo.mongodb.multipleCellCollectionsApproach.m_BusinessObjects;
//import gov.energy.nrel.dataRepositoryApp.dao.mongodb.TestUsingTestData;
//import org.junit.*;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.SpringApplicationConfiguration;
//import org.springframework.http.MediaType;
//import org.springframework.http.converter.HttpMessageConverter;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
//import org.springframework.web.context.WebApplicationContext;
//
//import java.nio.charset.Charset;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = CARApplication.class)
//@WebAppConfiguration
//public class EndpointsDatasetsTest extends TestUsingTestData {
//
//	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
//			MediaType.APPLICATION_JSON.getSubtype(),
//			Charset.forName("utf8"));
//
//	private MockMvc mockMvc;
//	private HttpMessageConverter mappingJackson2HttpMessageConverter;
//
//	@Autowired
//	private WebApplicationContext webApplicationContext;
//
//
//	@BeforeClass
//	public static void beforeClass() {
//
//		TestUsingTestData.beforeClass();
//	}
//
//	@AfterClass
//	public static void afterClass() {
//		TestUsingTestData.afterClass();
//	}
//
//	@Before
//	public void before() {
//
//		Settings settings = new Settings_forUnitTestPurposes();
//		initializeBusinessObjects(settings, settings);
//
//		super.before();
//
//		this.mockMvc = webAppContextSetup(webApplicationContext).build();
//	}
//
//	@After
//	public void after() {
//		super.after();
//	}
//
//	@Test
//	public void contextLoads() {
//	}
//
//
//	protected void initializeBusinessObjects(Settings settings, Settings settings_forUnitTestPurposes) {
//
//		Application.setBusinessObjects(new m_BusinessObjects(settings, settings_forUnitTestPurposes));
//	}
//
////	@Test
//	public void testSeedTestData() throws Exception {
//
////		String url = "/getDataset/" + TestData.objectId_1 + "?inTestMode=true";
//
//		String url = "/api/v01/seedTestData";
//
//		MockHttpServletRequestBuilder requestBuilder = get(url);
//
//		mockMvc.perform(requestBuilder)
//				.andExpect(status().isOk());
//
////				.andExpect(content().contentType(contentType));
//
////				.andExpect(jsonPath("dataset", is(this.bookmarkList.get(0).getId().intValue())))
////				.andExpect(jsonPath("$.id", is(this.bookmarkList.get(0).getId().intValue())))
////				.andExpect(jsonPath("$.uri", is("http://bookmark.com/1/" + userName)))
////				.andExpect(jsonPath("$.description", is("A description")));
//	}
//
//
////	@Test
//	public void testGetRowsQuery() throws Exception {
//
//		String url = "/api/v01/rows?inTestMode=true";
//
//		MockHttpServletRequestBuilder requestBuilder = post(url);
//		requestBuilder.content(
//				"[ {name: 'Some Column Name', value: 4, comparisonOperator: 'EQUALS'}, " +
//						"{name: 'Float Values Column Name', value: 4.55, comparisonOperator: 'EQUALS'}, " +
//						"{name: 'Additional new Column Name 2', value: 'b4', comparisonOperator: 'EQUALS'}," +
//						"{name: '_submitter', value: 'Submitter 2', comparisonOperator: 'EQUALS'} " +
//						"]");
//
//		mockMvc.perform(requestBuilder)
//				.andExpect(status().isOk());
//
////				.andExpect(content().contentType(contentType));
//
////				.andExpect(jsonPath("dataset", is(this.bookmarkList.get(0).getId().intValue())))
////				.andExpect(jsonPath("$.id", is(this.bookmarkList.get(0).getId().intValue())))
////				.andExpect(jsonPath("$.uri", is("http://bookmark.com/1/" + userName)))
////				.andExpect(jsonPath("$.description", is("A description")));
//	}
//}
