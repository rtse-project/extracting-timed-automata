package testsRoutes;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import server.HttpServerConverter;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class TestGetAllFileByType {

	static HttpServerConverter server;
	static String base_url;
	static String base_project;
	static final String db_name = "ttAllfiles";

	@BeforeClass
	public static void setUp() throws Exception {
		server = new HttpServerConverter();
		base_url = "http://localhost:" + HttpServerConverter.getPort();
		ClassLoader classLoader = TestGetAllFileByType.class.getClassLoader();
		File file = new File(classLoader.getResource("progs/Attempt1.java").getFile());
		base_project = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("/")) + "/";
		MongoOptions.getInstance().setDbName("tt");
		MongoConnector.getInstance().drop();
		MongoConnector.getInstance().ensureIndexes();
		TestGetFile.openProject(db_name, base_project);
		base_url = base_url + "/getFilesByType";
	}

	@AfterClass
	public static void tearDown() throws Exception {
		MongoConnector.getInstance().close();
		server.stop();
	}

	@Test
	public void TestSuccess() throws Exception {

		MongoOptions.getInstance().setDbName("tt");
		MongoConnector.getInstance().drop();
		MongoConnector.getInstance().ensureIndexes();
		//first open project and get indexes
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost("http://localhost:" + HttpServerConverter.getPort() + "/openProject");
		List<NameValuePair> nvps = new ArrayList<>();
		nvps.add(new BasicNameValuePair("name", db_name));
		nvps.add(new BasicNameValuePair("path", "file://" + base_project));
		httppost.setEntity(new UrlEncodedFormEntity(nvps));
		CloseableHttpResponse response = httpclient.execute(httppost);
		InputStream stream = response.getEntity().getContent();
		String myString = IOUtils.toString(stream, "UTF-8");
		//System.out.println(myString);
		assertEquals(200, response.getStatusLine().getStatusCode());

		int status = 0;
		long start = System.currentTimeMillis();
		boolean expired = false;
		while(status == 0 && !expired){
			httpclient = HttpClients.createDefault();
			httppost = new HttpPost("http://localhost:" + HttpServerConverter.getPort() + "/isProjectOpen");
			nvps = new ArrayList<>();
			nvps.add(new BasicNameValuePair("name", db_name));
			httppost.setEntity(new UrlEncodedFormEntity(nvps));
			response = httpclient.execute(httppost);
			stream = response.getEntity().getContent();
			myString = IOUtils.toString(stream, "UTF-8");
			TestMains.Item itemWithOwner = new ObjectMapper().readValue(myString, TestMains.Item.class);
			status = itemWithOwner.getStatus();
			long now = System.currentTimeMillis() - start;
			if( now > 15*1000){
				expired = true; //max 30s
			}
		}
		if(!expired){
			httpclient = HttpClients.createDefault();
			httppost = new HttpPost(base_url);
			nvps = new ArrayList<>();
			nvps.add(new BasicNameValuePair("name", db_name));
			nvps.add(new BasicNameValuePair("type", "Object"));
			httppost.setEntity(new UrlEncodedFormEntity(nvps));
			response = httpclient.execute(httppost);
			stream = response.getEntity().getContent();
			myString = IOUtils.toString(stream, "UTF-8");
			assertEquals(200, response.getStatusLine().getStatusCode());
		} else {
			throw new TimeoutException("More than 15s");
		}
	}

	@Test
	public void TestGetAllFileByTypeNoPar() throws Exception {
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

	@Test
	public void TestGetAllFileByTypeNoType() throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(base_url);
		List<NameValuePair> nvps = new ArrayList<>();
		nvps.add(new BasicNameValuePair("name", db_name));
		httppost.setEntity(new UrlEncodedFormEntity(nvps));
		CloseableHttpResponse response = httpclient.execute(httppost);
		InputStream stream = response.getEntity().getContent();
		String myString = IOUtils.toString(stream, "UTF-8");
		//System.out.println(myString);
		assertEquals(400, response.getStatusLine().getStatusCode());
	}

	@Test
	public void TestGetAllFileByTypeNoIndexes() throws Exception {
		MongoOptions.getInstance().setDbName("tt");
		MongoConnector.getInstance().drop();
		MongoConnector.getInstance().ensureIndexes();

		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(base_url);
		List<NameValuePair> nvps = new ArrayList<>();
		nvps.add(new BasicNameValuePair("name", db_name));
		nvps.add(new BasicNameValuePair("type", "type"));
		httppost.setEntity(new UrlEncodedFormEntity(nvps));
		CloseableHttpResponse response = httpclient.execute(httppost);
		InputStream stream = response.getEntity().getContent();
		String myString = IOUtils.toString(stream, "UTF-8");
		//System.out.println(myString);
		assertEquals(200, response.getStatusLine().getStatusCode());
	}



}
