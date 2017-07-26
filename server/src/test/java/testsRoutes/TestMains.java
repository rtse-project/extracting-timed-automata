package testsRoutes;

import intermediateModelHelper.indexing.mongoConnector.MongoConnector;
import intermediateModelHelper.indexing.mongoConnector.MongoOptions;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import server.HttpServerConverter;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class TestMains {

	private static final String db_name = "ttMains";

	public static class Item {
		String status;

		public Item() {
		}

		public int getStatus() {
			return status.equals("0") ? 0 : 1;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public Item(@JsonProperty("status")String status) {
			this.status = status;
		}
	}

	static HttpServerConverter server;
	static String base_url;
	static String base_project;

	@BeforeClass
	public static void setUp() throws Exception {
		server = new HttpServerConverter();
		base_url = "http://localhost:" + HttpServerConverter.getPort();
		ClassLoader classLoader = TestMains.class.getClassLoader();
		File file = new File(classLoader.getResource("progs/Attempt1.java").getFile());
		base_project = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("/")) + "/";
		MongoOptions.getInstance().setDbName(db_name);
		MongoConnector.getInstance().drop();
		MongoConnector.getInstance().ensureIndexes();
		TestGetFile.openProject(db_name, base_project);
		base_url = "http://localhost:" + HttpServerConverter.getPort() + "/getMains";
	}


	@AfterClass
	public static void tearDown() throws Exception {
		MongoConnector.getInstance().close();
		server.stop();
	}

	@Test
	public void TestNoIndexes() throws Exception {
		MongoOptions.getInstance().setDbName("tt");
		MongoConnector.getInstance().drop();
		MongoConnector.getInstance().ensureIndexes();
		//first open project and get indexes
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(base_url);
		List<NameValuePair> nvps = new ArrayList<>();
		nvps.add(new BasicNameValuePair("name", db_name));
		nvps.add(new BasicNameValuePair("path", "file://" + base_project));
		httppost.setEntity(new UrlEncodedFormEntity(nvps));
		CloseableHttpResponse response = httpclient.execute(httppost);
		InputStream stream = response.getEntity().getContent();
		String myString = IOUtils.toString(stream, "UTF-8");
		//System.out.println(myString);
		assertEquals(200, response.getStatusLine().getStatusCode());
	}

	@Test
	public void TestMains() throws Exception {

		MongoOptions.getInstance().setDbName("tt");
		MongoConnector.getInstance().drop();
		MongoConnector.getInstance().ensureIndexes();

		//first open project and get indexes
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost("http://localhost:" + HttpServerConverter.getPort() + "/openProject");
		List<NameValuePair> nvps = new ArrayList<>();
		nvps.add(new BasicNameValuePair("name", db_name));
		nvps.add(new BasicNameValuePair("path", "file://" + base_project));
		nvps.add(new BasicNameValuePair("invalidCache", "1"));
		httppost.setEntity(new UrlEncodedFormEntity(nvps));
		CloseableHttpResponse response = httpclient.execute(httppost);
		InputStream stream = response.getEntity().getContent();
		String myString = IOUtils.toString(stream, "UTF-8");
		//System.out.println(myString);
		assertEquals(200, response.getStatusLine().getStatusCode());

		int status = 0;
		while(status == 0){
			httpclient = HttpClients.createDefault();
			httppost = new HttpPost("http://localhost:" + HttpServerConverter.getPort() + "/isProjectOpen");
			nvps = new ArrayList<>();
			nvps.add(new BasicNameValuePair("name", db_name));
			httppost.setEntity(new UrlEncodedFormEntity(nvps));
			response = httpclient.execute(httppost);
			stream = response.getEntity().getContent();
			myString = IOUtils.toString(stream, "UTF-8");
			Item itemWithOwner = new ObjectMapper().readValue(myString, Item.class);
			status = itemWithOwner.getStatus();
		}

		//Get Mains
		httpclient = HttpClients.createDefault();
		httppost = new HttpPost(base_url);
		nvps = new ArrayList<>();
		nvps.add(new BasicNameValuePair("name", db_name));
		httppost.setEntity(new UrlEncodedFormEntity(nvps));
		response = httpclient.execute(httppost);
		stream = response.getEntity().getContent();
		myString = IOUtils.toString(stream, "UTF-8");
		//System.out.println(myString);
		assertEquals(200, response.getStatusLine().getStatusCode());
	}

	@Test
	public void TestMainsNoPar() throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(base_url);
		List<NameValuePair> nvps = new ArrayList<>();
		//nvps.add(new BasicNameValuePair("name", "tt"));
		httppost.setEntity(new UrlEncodedFormEntity(nvps));
		CloseableHttpResponse response = httpclient.execute(httppost);
		InputStream stream = response.getEntity().getContent();
		String myString = IOUtils.toString(stream, "UTF-8");
		//System.out.println(myString);
		assertEquals(406, response.getStatusLine().getStatusCode());
	}


}
