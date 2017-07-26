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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class TestGetFile {

	static HttpServerConverter server;
	static String base_url;
	static String base_project;
	static String base_file;
	static final String db_name = "ttGetFile";

	@BeforeClass
	public static void setUp() throws Exception {
		server = new HttpServerConverter();
		Thread.sleep(1000);
		//server.setDebug(true);
		base_url = "http://localhost:" + HttpServerConverter.getPort();
		ClassLoader classLoader = TestGetFile.class.getClassLoader();
		File file = new File(classLoader.getResource("progs/Attempt1.java").getFile());
		base_project = file.getAbsolutePath();
		base_project = base_project.substring(0, base_project.lastIndexOf("/")) + "/";
		base_file = "Attempt1.java";
		MongoOptions.getInstance().setDbName(db_name);
		MongoConnector.getInstance().drop();
		MongoConnector.getInstance().ensureIndexes();
		openProject(db_name, base_project);

	}


	public static void openProject(String db, String projectpath) throws IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost("http://localhost:" + HttpServerConverter.getPort() + "/openProject");
		List<NameValuePair> nvps = new ArrayList<>();
		nvps.add(new BasicNameValuePair("name", db));
		nvps.add(new BasicNameValuePair("path", "file://" + projectpath));
		httppost.setEntity(new UrlEncodedFormEntity(nvps));
		CloseableHttpResponse response = httpclient.execute(httppost);
		InputStream stream = response.getEntity().getContent();
		String myString = IOUtils.toString(stream, "UTF-8");
		//System.out.println(myString);
		assertEquals(200, response.getStatusLine().getStatusCode());
		waitForEndOpen(db);
	}

	public static void waitForEndOpen(String db) throws IOException {
		CloseableHttpClient httpclient;
		HttpPost httppost;
		List<NameValuePair> nvps;
		CloseableHttpResponse response;
		InputStream stream;
		String myString;
		int status = 0;
		while(status == 0){
			httpclient = HttpClients.createDefault();
			httppost = new HttpPost("http://localhost:" + HttpServerConverter.getPort() + "/isProjectOpen");
			nvps = new ArrayList<>();
			nvps.add(new BasicNameValuePair("name", db));
			httppost.setEntity(new UrlEncodedFormEntity(nvps));
			response = httpclient.execute(httppost);
			stream = response.getEntity().getContent();
			myString = IOUtils.toString(stream, "UTF-8");
			TestMains.Item itemWithOwner = new ObjectMapper().readValue(myString, TestMains.Item.class);
			status = itemWithOwner.getStatus();
		}
	}

	@AfterClass
	public static void tearDown() throws Exception {
		MongoConnector.getInstance().close();
		server.stop();
	}

	@Test
	public void TestGetFileSuccess() throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(base_url + "/getFile");
		List<NameValuePair> nvps = new ArrayList<>();
		nvps.add(new BasicNameValuePair("filePath", "file://" + base_file));
		nvps.add(new BasicNameValuePair("name", db_name));
		httppost.setEntity(new UrlEncodedFormEntity(nvps));
		CloseableHttpResponse response = httpclient.execute(httppost);
		InputStream stream = response.getEntity().getContent();
		String myString = IOUtils.toString(stream, "UTF-8");
		//System.out.println(myString);
		assertEquals(200, response.getStatusLine().getStatusCode());

	}
	@Test
	public void TestGetFileNoFileURI() throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(base_url + "/getFile");
		List<NameValuePair> nvps = new ArrayList<>();
		nvps.add(new BasicNameValuePair("filePath", "" + base_file));
		nvps.add(new BasicNameValuePair("name", db_name));
		httppost.setEntity(new UrlEncodedFormEntity(nvps));
		CloseableHttpResponse response = httpclient.execute(httppost);
		InputStream stream = response.getEntity().getContent();
		String myString = IOUtils.toString(stream, "UTF-8");
		assertEquals(400, response.getStatusLine().getStatusCode());
	}
	@Test
	public void TestGetFileNoFile() throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(base_url + "/getFile");
		List<NameValuePair> nvps = new ArrayList<>();
		nvps.add(new BasicNameValuePair("filePath", "file://C:/file.java"));
		nvps.add(new BasicNameValuePair("name", db_name));
		httppost.setEntity(new UrlEncodedFormEntity(nvps));
		CloseableHttpResponse response = httpclient.execute(httppost);
		InputStream stream = response.getEntity().getContent();
		String myString = IOUtils.toString(stream, "UTF-8");
		assertEquals(400, response.getStatusLine().getStatusCode());
	}
	@Test
	public void TestGetFileNoParam() throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(base_url + "/getFile");
		List<NameValuePair> nvps = new ArrayList<>();
		//nvps.add(new BasicNameValuePair("filePath", "file://C:/file.java"));
		httppost.setEntity(new UrlEncodedFormEntity(nvps));
		CloseableHttpResponse response = httpclient.execute(httppost);
		InputStream stream = response.getEntity().getContent();
		String myString = IOUtils.toString(stream, "UTF-8");
		assertEquals(406, response.getStatusLine().getStatusCode());
	}
	@Test
	public void TestGetFileNoFilePar() throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(base_url + "/getFile");
		List<NameValuePair> nvps = new ArrayList<>();
		//nvps.add(new BasicNameValuePair("filePath", "file://C:/file.java"));
		nvps.add(new BasicNameValuePair("name", db_name));
		httppost.setEntity(new UrlEncodedFormEntity(nvps));
		CloseableHttpResponse response = httpclient.execute(httppost);
		InputStream stream = response.getEntity().getContent();
		String myString = IOUtils.toString(stream, "UTF-8");
		assertEquals(400, response.getStatusLine().getStatusCode());
	}
}
