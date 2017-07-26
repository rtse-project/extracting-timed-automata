package testTimeVar;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import intermediateModel.interfaces.TimeUnit;
import intermediateModelHelper.indexing.mongoConnector.MongoConnector;
import intermediateModelHelper.indexing.mongoConnector.MongoOptions;
import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.HttpServerConverter;
import server.routes.v1.Routes;
import testsRoutes.TestMains;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class TestTimeUnit {

	static HttpServerConverter server;
	static String base_url;
	static String base_project;

	@After
	public void tearDown() throws Exception {
		MongoConnector.getInstance().close();
		server.stop();
	}

	@Before
	public void setUp() throws Exception {
		server = new HttpServerConverter();
		base_url = "http://localhost:" + HttpServerConverter.getPort();
		ClassLoader classLoader = TestTimeUnit.class.getClassLoader();
		File file = new File(classLoader.getResource("exampleTime/Cache.java").getFile());
		base_project = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("/")) + "/";

		MongoOptions.getInstance().setDbName("tt");
		MongoConnector.getInstance().drop();
		MongoConnector.getInstance().ensureIndexes();

		openConnection();
	}

	private void openConnection() throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(base_url + Routes.OPEN_PROJECT);
		List<NameValuePair> nvps = new ArrayList<>();
		nvps.add(new BasicNameValuePair("name", "tt"));
		nvps.add(new BasicNameValuePair("path", "file://" + base_project));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nvps));
			CloseableHttpResponse response = httpclient.execute(httppost);
			InputStream stream = response.getEntity().getContent();
			String myString = IOUtils.toString(stream, "UTF-8");
		} catch (Exception e) {
			throw new Exception("Cannot connect to " + Routes.OPEN_PROJECT);
		}
		//System.out.println(myString);

		int status = 0;
		while(status == 0){
			httpclient = HttpClients.createDefault();
			httppost = new HttpPost("http://localhost:" + HttpServerConverter.getPort() + Routes.IS_PROJECT_OPEN);
			nvps = new ArrayList<>();
			nvps.add(new BasicNameValuePair("name", "tt"));
			httppost.setEntity(new UrlEncodedFormEntity(nvps));
			CloseableHttpResponse response = httpclient.execute(httppost);
			InputStream stream = response.getEntity().getContent();
			String myString = IOUtils.toString(stream, "UTF-8");
			TestMains.Item itemWithOwner = new ObjectMapper().readValue(myString, TestMains.Item.class);
			status = itemWithOwner.getStatus();
		}
	}



	@Test
	public void TestTimeVar() throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(base_url + Routes.GET_FILE);
		List<NameValuePair> nvps = new ArrayList<>();
		nvps.add(new BasicNameValuePair("name", "tt"));
		nvps.add(new BasicNameValuePair("filePath", "file://Cache.java"));
		String json;
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nvps));
			CloseableHttpResponse response = httpclient.execute(httppost);
			InputStream stream = response.getEntity().getContent();
			json = IOUtils.toString(stream, "UTF-8");
			//System.out.println(json);
		} catch (Exception e) {
			throw new Exception("Cannot connect to " + Routes.GET_FILE);
		}
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(json);
		JsonNode const_dec = root.at("/0/methods/0/declaredVar");
		JsonNode const_tim = root.at("/0/methods/0/timeVars");
		JsonNode meth_dec = root.at("/0/methods/1/declaredVar");
		JsonNode meth_tim = root.at("/0/methods/1/timeVars");
		assertEquals(const_dec.size(), 0);
		assertEquals(const_tim.size(), 1);
		assertEquals(meth_dec.size(), 1);
		assertEquals(meth_tim.size(), 2);
		assertEquals("now", meth_tim.at("/0").asText());
		assertEquals("lastRefresh", meth_tim.at("/1").asText());
	}

	@Test
	public void TestTimeUnitMethod() throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(base_url + Routes.GET_FILE);
		List<NameValuePair> nvps = new ArrayList<>();
		nvps.add(new BasicNameValuePair("name", "tt"));
		nvps.add(new BasicNameValuePair("filePath", "file://Cache.java"));
		String json;
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nvps));
			CloseableHttpResponse response = httpclient.execute(httppost);
			InputStream stream = response.getEntity().getContent();
			json = IOUtils.toString(stream, "UTF-8");
			//System.out.println(json);
		} catch (Exception e) {
			throw new Exception("Cannot connect to " + Routes.GET_FILE);
		}
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(json);
		JsonNode m = root.at("/0/methods/1");
		assertEquals(TimeUnit.UNKNOWN.toString().toLowerCase() ,m.at("/returnTypeUnit").asText().toLowerCase());
	}

	@Test
	public void TestTimeUnitParameter() throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(base_url + Routes.GET_FILE);
		List<NameValuePair> nvps = new ArrayList<>();
		nvps.add(new BasicNameValuePair("name", "tt"));
		nvps.add(new BasicNameValuePair("filePath", "file://Cache.java"));
		String json;
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nvps));
			CloseableHttpResponse response = httpclient.execute(httppost);
			InputStream stream = response.getEntity().getContent();
			json = IOUtils.toString(stream, "UTF-8");
			//System.out.println(json);
		} catch (Exception e) {
			throw new Exception("Cannot connect to " + Routes.GET_FILE);
		}
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(json);
		JsonNode m = root.at("/0/methods/2/parameters");
		assertEquals(1, m.size());
		assertEquals(TimeUnit.UNKNOWN.toString().toLowerCase(), m.at("/0/unit").asText().toLowerCase());
	}

}
