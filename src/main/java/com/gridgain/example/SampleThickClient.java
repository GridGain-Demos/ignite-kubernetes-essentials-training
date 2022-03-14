package com.gridgain.example;

import java.util.List;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.FieldsQueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.client.ClientException;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.kubernetes.TcpDiscoveryKubernetesIpFinder;

/**
 * The sample app connects to a K8 cluster and executes a simple SQL query. It can work as an external app that is
 * not deployed in K8 or as a containarized pod. For the second, you need to create a Docker image and K8 configuration.
 */
public class SampleThickClient {

	private static final String QUERY = "SELECT city.name, MAX(city.population), country.name, country.GovernmentForm FROM country "
			+ "JOIN city ON city.countrycode = country.code "
			+ "GROUP BY city.name, country.name, country.GovernmentForm, city.population "
			+ "ORDER BY city.population DESC LIMIT ?";

    public static void main(String[] args) {
		new SampleThickClient();
	}

	public SampleThickClient() {
		IgniteConfiguration configuration = new ThickClientConfiguration();

		try (Ignite ignite = Ignition.start(configuration);) {
			IgniteCache<CityKey, City> cities = ignite.getOrCreateCache("City");

			SqlFieldsQuery query = new SqlFieldsQuery(QUERY).setSchema("PUBLIC").setArgs(10);

			System.out.println("Running Query;");
			System.out.println(QUERY);
			System.out.println();

			FieldsQueryCursor<List<?>> cursor = cities.query(query);

            cursor.getAll().forEach(columns -> {
                System.out.println(columns.get(0) + ", population = " + columns.get(1) + ", country=" + columns.get(2));
            });

        } catch (ClientException e) {
            System.err.println(e.getMessage());
        }
    }

	public class ThickClientConfiguration extends IgniteConfiguration {
		public ThickClientConfiguration() {
			setClientMode(true);
			setPeerClassLoadingEnabled(true);

			TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();

			TcpDiscoveryKubernetesIpFinder kubernetesIpFinder = new TcpDiscoveryKubernetesIpFinder();
			kubernetesIpFinder.setNamespace("ignite-namespace");
			kubernetesIpFinder.setServiceName("ignite-service");

			discoverySpi.setIpFinder(kubernetesIpFinder);
			setDiscoverySpi(discoverySpi);
		}
	}
}
