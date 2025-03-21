package edu.ycp.cs320.tbag.main;

import java.io.File;
import org.eclipse.jetty.server.Server;

public class Main {
    public static void main(String[] args) throws Exception {
        // Path to your web application directory.
        // In this example, we assume your web resources are in the WebContent folder.
        String webappCodeBase = "./WebContent";
        File webAppDir = new File(webappCodeBase);
        
        // Create a new instance of the Launcher, which sets up and configures Jetty.
        Launcher launcher = new Launcher();
        
        // Create the server on port 8081 with context path "/TBAG"
        System.out.println("CREATING: web server on port 8081");
        Server server = launcher.launch(true, 8081, webAppDir.getAbsolutePath(), "/TBAG");
        
        // Start the server.
        System.out.println("STARTING: web server on port 8081");
        server.start();
        
        // Dump any standard error messages (this may output warnings; often normal during startup).
        server.dumpStdErr();
        
        // Inform the user that the server is running.
        System.out.println("RUNNING: web server on port 8081");
        
        // Wait for the server to stop running.
        server.join();
    }
}
