package testsRoutes;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.junit.*;
import org.junit.Test;
import server.HttpServerConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class TestBasicUrl {

	static HttpServerConverter server;
	static String base_url;
	private static String base_project;

	@BeforeClass
	public static void setUp() throws Exception {
		server = new HttpServerConverter();
		base_url = "http://localhost:" + HttpServerConverter.getPort();
		ClassLoader classLoader = TestGetAllFile.class.getClassLoader();
		File file = new File(classLoader.getResource("progs/Attempt1.java").getFile());
		base_project = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("/")) + "/";
	}

	@AfterClass
	public static void tearDown() throws Exception {
		server.stop();
	}

	//basic test of warm up
	@Test
	public void TestRoot() throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(base_url + "/");
		CloseableHttpResponse response = httpclient.execute(httpget);
		assertEquals(200, response.getStatusLine().getStatusCode());
	}
	@Test
	public void TestEchoHeader() throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(base_url + "/test/echoHeader");
		CloseableHttpResponse response = httpclient.execute(httpget);
		assertEquals(200, response.getStatusLine().getStatusCode());
	}
	@Test
	public void TestEchoGet() throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(base_url + "/test/echoGet?test=var");
		CloseableHttpResponse response = httpclient.execute(httpget);
		assertEquals(200, response.getStatusLine().getStatusCode());
	}
	@Test
	public void TestEchoPost() throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(base_url + "/test/echoPost");
		List<NameValuePair> nvps = new ArrayList<>();
		nvps.add(new BasicNameValuePair("projectPath", "file://" + base_project));
		nvps.add(new BasicNameValuePair("skipTest", "1"));
		httppost.setEntity(new UrlEncodedFormEntity(nvps));
		CloseableHttpResponse response = httpclient.execute(httppost);
		assertEquals(200, response.getStatusLine().getStatusCode());
	}
}
