package servlets;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.After;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;

import dbProcs.Constants;
import dbProcs.Getter;
import dbProcs.GetterTest;
import dbProcs.Setter;
import testUtils.TestProperties;
import utils.InstallationException;

public class SetupIT {
	private static String lang = "en_GB";
	private static org.apache.log4j.Logger log = Logger.getLogger(SetupIT.class);
	private static String applicationRoot = new String();
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private StringBuffer dbProp = new StringBuffer();

	/**
	 * Initialize directories
	 */
	@BeforeClass
	public static void setDirectory() {
		TestProperties.setTestPropertiesFileDirectory(log);
	}

	@Before
	public void setup() {
		log.debug("Setting Up Blank Request and Response");
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();

	}
	
	@After
	public void tearDown() throws IOException {
		log.debug("Cleaning up");

		ensureDatabaseProps();
	}	

	private void removeDatabaseProps() {
		FileUtils.deleteQuietly(new File(Constants.DBPROP));
	}

	private void ensureDatabaseProps() throws IOException {
		FileUtils.deleteQuietly(new File(Constants.DBPROP));
		Files.write(Paths.get(Constants.DBPROP), dbProp.toString().getBytes(), StandardOpenOption.CREATE);
	}

	@Test
	public void testCoreCreation() {

		log.debug("Creating Setup Servlet Instance");
		Setup servlet = new Setup();
		try {
			servlet.init(new MockServletConfig("Setup"));
		} catch (ServletException e) {
			fail(e.toString());
		}

		removeDatabaseProps();
		Setup.isInstalled();

		String authData = "";
		try {
			authData = FileUtils.readFileToString(new File(Constants.SETUP_AUTH), StandardCharsets.UTF_8);
		} catch (IOException e) {
			fail(e.toString());
		}

		request.getSession().setAttribute("lang", lang);

		request.getSession().setAttribute("dbhost", "localhost");
		request.getSession().setAttribute("dbport", 3306);
		request.getSession().setAttribute("dbuser", "root");
		request.getSession().setAttribute("dbpass", "");
		request.getSession().setAttribute("dbauth", authData);

		log.debug("Running doPost");
		try {
			servlet.doPost(request, response);
		} catch (ServletException | IOException e) {

			fail(e.toString());

		}

		String location = response.getHeader("Location");
		if (!location.endsWith("login.jsp")) {
			fail("Setup not Redirecting to login.jsp.");
		}

	}

	@Test
	public void testAuthCreation() {

		assertFalse(!Setup.isInstalled());

		String authData = "";
		try {
			authData = FileUtils.readFileToString(new File(Constants.SETUP_AUTH), StandardCharsets.UTF_8);
		} catch (IOException e) {
			fail(e.toString());
		}

		if (authData == "") {
			fail("No auth data found.");
		}

	}
}
