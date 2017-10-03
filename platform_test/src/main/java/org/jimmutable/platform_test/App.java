package org.jimmutable.platform_test;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.CloudExecutionEnvironment;

import com.digitalpanda.auth.UserManager;
import com.digitalpanda.server_core.ServerCoreTypeNameRegister;

/**
 * Hello world!
 *
 */
public class App
{

	private static final Logger logger = LogManager.getLogger(App.class);

	public static void main(String[] args) throws Exception
	{

		org.eclipse.jetty.util.log.Log.setLog(new Jetty2Log4j2Bridge("jetty"));

		CloudExecutionEnvironment.startupIntegrationTest(new ApplicationId("platform_test"));
		ServerCoreTypeNameRegister.registerAllTypes();
		UserManager.startup();

		logger.info("ABC!!!!!");

		// 1. Creating the server on port 8080
		Server server = new Server(8080);

		// 2. Creating the WebAppContext for the created content
		WebAppContext context = new WebAppContext();
		context.setResourceBase("src/main/webapp");
		context.setContextPath("/");

		context.addFilter(AuthFilter.class, "/app/*", EnumSet.of(DispatcherType.INCLUDE, DispatcherType.REQUEST));

		context.addServlet(IndexServlet.class, "/app/home");
		context.addServlet(LoginServlet.class, "/login");

		// ctx.setParentLoaderPriority(true);

		// 3. Including the JSTL jars for the webapp.

		context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/[^/]*jstl.*\\.jar$");

		// 4. Enabling the Annotation based configuration
		org.eclipse.jetty.webapp.Configuration.ClassList classlist = org.eclipse.jetty.webapp.Configuration.ClassList.setServerDefault(server);
		classlist.addAfter("org.eclipse.jetty.webapp.FragmentConfiguration", "org.eclipse.jetty.plus.webapp.EnvConfiguration", "org.eclipse.jetty.plus.webapp.PlusConfiguration");
		classlist.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration", "org.eclipse.jetty.annotations.AnnotationConfiguration");

		// 5. Setting the handler and starting the Server
		server.setHandler(context);
		server.start();

		// server.dump(System.err);

		server.join();
	}

}
