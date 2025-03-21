package edu.ycp.cs320.tbag.main;

import java.lang.management.ManagementFactory;

import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;

public class Launcher {
    /**
     * Launches an embedded Jetty server configured for the TBAG application.
     * 
     * @param fromEclipse true if running interactively from Eclipse
     * @param port        the port on which to run the server (e.g., 8081)
     * @param webappDir   the absolute path to the web application directory (e.g., WebContent)
     * @param contextPath the context path (e.g., "/TBAG")
     * @return a configured but not started Server instance
     * @throws Exception if an error occurs during server setup
     */
    public Server launch(boolean fromEclipse, int port, String webappDir, String contextPath) throws Exception {
        // Create a basic Jetty server object that listens on the specified port.
        Server server = new Server(port);
        
        // Setup JMX for monitoring.
        MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
        server.addBean(mbContainer);
        
        // Create a WebAppContext for your TBAG web application.
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath(contextPath);
        webapp.setWar(webappDir);
        
        // Set up the server's default configuration including annotations for JSP support.
        Configuration.ClassList classList = Configuration.ClassList.setServerDefault(server);
        classList.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
                              "org.eclipse.jetty.annotations.AnnotationConfiguration");
        
        // Configure container scanning for tag libraries, web fragments, etc.
        webapp.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$");
        
        // Disable directory listings.
        webapp.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        
        // Allow welcome file to be processed as a servlet.
        webapp.setInitParameter("org.eclipse.jetty.servlet.Default.welcomeServlets", "true");
        
        // Call a hook for additional configuration if needed.
        onCreateWebAppContext(webapp);
        
        // Set the web application context as the server's handler.
        server.setHandler(webapp);
        
        return server;
    }
    
    /**
     * Hook method to allow further customization of the WebAppContext.
     * Override this method if additional configuration is required.
     *
     * @param webapp the WebAppContext for your application
     */
    protected void onCreateWebAppContext(WebAppContext webapp) {
        // Additional custom configuration can be added here.
    }
}
