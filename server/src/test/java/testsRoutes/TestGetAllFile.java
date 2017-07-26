package testsRoutes;

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

import static org.junit.Assert.assertEquals;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class TestGetAllFile {

	static HttpServerConverter server;
	static String base_url;
	static String base_project;

	static final String db_name = "ttGetAllFile";

	@BeforeClass
	public static void setUp() throws Exception {
		server = new HttpServerConverter();
		base_url = "http://localhost:" + HttpServerConverter.getPort();
		ClassLoader classLoader = TestGetAllFile.class.getClassLoader();
		File file = new File(classLoader.getResource("progs/Attempt1.java").getFile());
		base_project = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("/")) + "/";

		MongoOptions.getInstance().setDbName(db_name);
		MongoConnector.getInstance().drop();
		MongoConnector.getInstance().ensureIndexes();

		TestGetFile.openProject(db_name, base_project);
	}

	@AfterClass
	public static void tearDown() throws Exception {
		MongoConnector.getInstance().close();
		server.stop();
	}

	@Test
	public void TestGetAllFiles() throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(base_url + "/getAllFiles");
		List<NameValuePair> nvps = new ArrayList<>();
		nvps.add(new BasicNameValuePair("name", db_name));
		nvps.add(new BasicNameValuePair("skipTest", "0"));
		httppost.setEntity(new UrlEncodedFormEntity(nvps));
		CloseableHttpResponse response = httpclient.execute(httppost);
		InputStream stream = response.getEntity().getContent();
		String myString = IOUtils.toString(stream, "UTF-8");
		assertEquals(200, response.getStatusLine().getStatusCode());

	}
	@Test
	public void TestGetAllFilesSkipTest() throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(base_url + "/getAllFiles");
		List<NameValuePair> nvps = new ArrayList<>();
		nvps.add(new BasicNameValuePair("name", db_name));
		nvps.add(new BasicNameValuePair("skipTest", "1"));
		httppost.setEntity(new UrlEncodedFormEntity(nvps));
		CloseableHttpResponse response = httpclient.execute(httppost);
		InputStream stream = response.getEntity().getContent();
		String myString = IOUtils.toString(stream, "UTF-8");
		assertEquals(200, response.getStatusLine().getStatusCode());

	}
	@Test
	public void TestGetAllFilesNoPar() throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(base_url + "/getAllFiles");
		List<NameValuePair> nvps = new ArrayList<>();
		//nvps.add(new BasicNameValuePair("projectPath", "file:///Users/giovanni/repository/java-xal/project_eval/"));
		//nvps.add(new BasicNameValuePair("skipTest", "1"));
		httppost.setEntity(new UrlEncodedFormEntity(nvps));
		CloseableHttpResponse response = httpclient.execute(httppost);
		InputStream stream = response.getEntity().getContent();
		String myString = IOUtils.toString(stream, "UTF-8");
		assertEquals(406, response.getStatusLine().getStatusCode());
	}
	@Test
	public void TestGetAllFilesNoSkip() throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(base_url + "/getAllFiles");
		List<NameValuePair> nvps = new ArrayList<>();
		nvps.add(new BasicNameValuePair("name", db_name));
		//nvps.add(new BasicNameValuePair("skipTest", "1"));
		httppost.setEntity(new UrlEncodedFormEntity(nvps));
		CloseableHttpResponse response = httpclient.execute(httppost);
		InputStream stream = response.getEntity().getContent();
		String myString = IOUtils.toString(stream, "UTF-8");
		assertEquals(400, response.getStatusLine().getStatusCode());
	}
	@Test
	public void TestGetAllFilesNoFileProtocol() throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(base_url + "/getAllFiles");
		List<NameValuePair> nvps = new ArrayList<>();
		nvps.add(new BasicNameValuePair("name", db_name));
		nvps.add(new BasicNameValuePair("skipTest", "1"));
		httppost.setEntity(new UrlEncodedFormEntity(nvps));
		CloseableHttpResponse response = httpclient.execute(httppost);
		InputStream stream = response.getEntity().getContent();
		String myString = IOUtils.toString(stream, "UTF-8");
		assertEquals(200, response.getStatusLine().getStatusCode());
	}

}
