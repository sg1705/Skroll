package com.skroll.rest;


import com.google.inject.Module;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import com.skroll.rest.benchmark.BenchmarkAPI;
import com.skroll.util.SkrollGuiceModule;
import com.skroll.viewer.DocView;
import com.squarespace.jersey2.guice.BootstrapUtils;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;
import org.eclipse.jetty.annotations.ServletContainerInitializersStarter;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.LoggerFactory;
import javax.servlet.DispatcherType;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.*;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Example of using JSP's with embedded jetty and not requiring
 * all of the overhead of a WebAppContext.
 * For using the WebServer, edit Run Configuration and in working directory setting
 * append "Pipeline" towards the end.
 */

public class WebServer {
    public static final org.slf4j.Logger logger = LoggerFactory.getLogger(WebServer.class);
    private static final Logger LOG = Logger.getLogger(WebServer.class.getName());
    public static String BASE_URI = "src/main/webapp";

    private int port;
    private Server server;
    private URI serverURI;
    private Module skrollModule;

    public static void main(String[] args) throws Exception {
        int port = 8088;
        logger.info("Main arguments:" + Arrays.toString(args));
        if (args != null && args.length > 1) {
            if (args[0].equals("--port")) {
                if (args[1] != null)
                    port = Integer.parseInt(args[1]);
            }

            if (args[2].equals("--baseuri")) {
                if (args[3] != null)
                    BASE_URI = args[3];
            }

        }
        //default module
        Module module = new SkrollGuiceModule();
        WebServer main = new WebServer(port, module);
        main.start();
        main.waitForInterrupt();
    }


    public WebServer(int port, Module module) {
        this.port = port;
        this.skrollModule = module;
    }

    public void start() throws Exception {
        ServiceLocator locator = BootstrapUtils.newServiceLocator();
        BootstrapUtils.newInjector(locator, Arrays.asList(skrollModule, new ServletModule()));
        BootstrapUtils.install(locator);
        //installServletModule();
        server = new Server();
        ServerConnector connector = connector();

        server.addConnector(connector);

        URI baseUri = getWebRootResourceUri();
        WebAppContext webAppContext = getWebAppContext(baseUri);
        FilterHolder filterHolder = new FilterHolder(GuiceFilter.class);
        webAppContext.addFilter(filterHolder, "/*", EnumSet.of(DispatcherType.INCLUDE, DispatcherType.REQUEST));
        webAppContext.addServlet(this.getJerseyServlet(), "/restServices/*");
        webAppContext.addServlet(this.getDocViewServlet(), "/doc/*");
        server.setHandler(webAppContext);
        // Start Server
        server.start();

        // Show server state
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine(server.dump());
        }
        this.serverURI = getServerUri(connector);
    }

    private ServerConnector connector() {
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        return connector;
    }

    private URI getWebRootResourceUri() throws FileNotFoundException, URISyntaxException {
        URI indexUri = new File(BASE_URI).toURI();
        LOG.info("indexUri:" + indexUri);
        return indexUri;
    }

    /**
     * Setup the basic application "context" for this application at "/"
     * This is also known as the handler tree (in jetty speak)
     */
    private WebAppContext getWebAppContext(URI baseUri) {
        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setResourceBase(baseUri.toASCIIString());
        context.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());
        context.addBean(new ServletContainerInitializersStarter(context), true);
        context.addServlet(defaultServletHolder(baseUri), "/");
//        context.setConfigurations(new Configuration[] {
//                new AnnotationConfiguration()});
        return context;
    }

    /**
     * Create Default Servlet (must be named "default")
     */
    private ServletHolder defaultServletHolder(URI baseUri) {
        ServletHolder holderDefault = new ServletHolder("default", DefaultServlet.class);
        LOG.info("Base URI: " + baseUri);
        holderDefault.setInitParameter("resourceBase", baseUri.toASCIIString());
        holderDefault.setInitParameter("dirAllowed", "true");
        holderDefault.setInitParameter("cacheControl", "no-transform,public,max-age=3600,s-maxage=86400");
        return holderDefault;
    }

    /**
     * Establish the Server URI
     */
    private URI getServerUri(ServerConnector connector) throws URISyntaxException {
        String scheme = "http";
        for (ConnectionFactory connectFactory : connector.getConnectionFactories()) {
            if (connectFactory.getProtocol().equals("SSL-http")) {
                scheme = "https";
            }
        }
        String host = connector.getHost();
        if (host == null) {
            host = "localhost";
        }
        int port = connector.getLocalPort();
        serverURI = new URI(String.format("%s://%s:%d/", scheme, host, port));
        LOG.info("Server URI: " + serverURI);
        return serverURI;
    }

    public void stop() throws Exception {
        server.stop();
    }

    /**
     * Cause server to keep running until it receives a Interrupt.
     * <p>
     * Interrupt Signal, or SIGINT (Unix Signal), is typically seen as a result of a kill -TERM {pid} or Ctrl+C
     */
    public void waitForInterrupt() throws InterruptedException {
        server.join();
    }


    private ServletHolder getJerseyServlet() {
        ResourceConfig config = new ResourceConfig();
        config.register(DocAPI.class);
        config.register(BenchmarkAPI.class);
        config.register(MultiPartFeature.class);
        config.register(InstrumentAPI.class);
        ServletContainer container = new ServletContainer(config);
        return new ServletHolder(container);
    }

    /**
     * Adds DocView servlet to Jersey config
     * @return
     */
    private ServletHolder getDocViewServlet() {
        ResourceConfig config = new ResourceConfig();
        config.register(DocView.class);
        ServletContainer container = new ServletContainer(config);
        return new ServletHolder(container);
    }
}
