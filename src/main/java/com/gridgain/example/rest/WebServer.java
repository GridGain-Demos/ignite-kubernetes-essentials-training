package com.gridgain.example.rest;

import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;

import java.io.Closeable;
import java.util.HashSet;

import org.apache.ignite.configuration.DeploymentMode;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.kubernetes.TcpDiscoveryKubernetesIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class WebServer implements Runnable, Closeable {

	public static final int PORT = 8088;

	private Server server;

	public static void main(String[] args) throws Exception {
		IgniteConfiguration configuration;
		if (args.length > 0 && args[0].equals("local")) {
			System.out.println("Using local configuration");
			configuration = localConfiguration();
		} else {
			System.out.println("Using Kubernetes configuration");
			configuration = k8Configuration();
		}
		try (WebServer webServer = new WebServer(configuration)) {
			webServer.run();
		}
	}

	public WebServer(IgniteConfiguration configuration) throws Exception {
		server = new Server(PORT);
		ServletContextHandler servletContextHandler = new ServletContextHandler(NO_SESSIONS);

		servletContextHandler.setContextPath("/");
		server.setHandler(servletContextHandler);

		servletContextHandler.addServlet(new ServletHolder(new CityServlet(configuration)), "/cities");

	}

	@Override
	public void run() {
		try {
			server.start();
			System.out.println("Started Webserver on port " + PORT);
			System.out.println("http://localhost:" + PORT + "/cities");
			server.join();
		} catch (Exception ex) {
			System.err.println("Error occurred while starting Jetty " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void close() {
		try {
			server.stop();
			server.destroy();
			System.out.println("Stopped Webserver");
		} catch (Exception e) {
			// e.printStackTrace();
		}

	}

	private static IgniteConfiguration localConfiguration() {
		IgniteConfiguration configuration = new IgniteConfiguration();
		configuration.setClientMode(true);
		configuration.setDeploymentMode(DeploymentMode.CONTINUOUS);
		configuration.setPeerClassLoadingEnabled(true);

		TcpDiscoverySpi discoverySpi = new org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi();

		TcpDiscoveryVmIpFinder tcpDiscoveryVmIpFinder = new org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder();
		HashSet<String> addrs = new HashSet<String>();
		addrs.add("127.0.0.1:47500..47510");
		tcpDiscoveryVmIpFinder.setAddresses(addrs);
		discoverySpi.setIpFinder(tcpDiscoveryVmIpFinder);

		configuration.setDiscoverySpi(discoverySpi);
		return configuration;
	}

	private static IgniteConfiguration k8Configuration() {
		IgniteConfiguration configuration = new IgniteConfiguration();

		configuration.setClientMode(true);
		configuration.setPeerClassLoadingEnabled(true);

		TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();

		TcpDiscoveryKubernetesIpFinder kubernetesIpFinder = new TcpDiscoveryKubernetesIpFinder();
		kubernetesIpFinder.setNamespace("ignite-namespace");
		kubernetesIpFinder.setServiceName("ignite-service");

		discoverySpi.setIpFinder(kubernetesIpFinder);
		configuration.setDiscoverySpi(discoverySpi);
		return configuration;
	}

}
