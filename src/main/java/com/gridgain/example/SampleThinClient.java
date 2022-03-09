package com.gridgain.example;

import java.util.List;

import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.FieldsQueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.client.ClientException;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.client.ThinClientKubernetesAddressFinder;
import org.apache.ignite.configuration.ClientConfiguration;
import org.apache.ignite.kubernetes.configuration.KubernetesConnectionConfiguration;

/**
 * The sample app connects to a K8 cluster and executes a simple SQL query. It can work as an external app that is
 * not deployed in K8 or as a containarized pod. For the second, you need to create a Docker image and K8 configuration.
 */
public class SampleThinClient {

    private static final boolean EXTERNAL_APP_MODE = true;
	private static final String QUERY = "SELECT city.name, MAX(city.population), country.name, country.GovernmentForm FROM country "
			+ "JOIN city ON city.countrycode = country.code "
			+ "GROUP BY city.name, country.name, country.GovernmentForm, city.population "
			+ "ORDER BY city.population DESC LIMIT ?";

	public static void main(String[] args) {
        ClientConfiguration cfg = EXTERNAL_APP_MODE ? externalAppCfg() : containarizedAppCfg();

        try (IgniteClient client = Ignition.startClient(cfg)) {
			SqlFieldsQuery query = new SqlFieldsQuery(QUERY).setSchema("PUBLIC").setArgs(10);
			System.out.println("Running Query;");
			System.out.println(QUERY);
			System.out.println();

            FieldsQueryCursor<List<?>> cursor = client.query(query);

            cursor.getAll().forEach(columns -> {
                System.out.println(columns.get(0) + ", population = " + columns.get(1) + ", country=" + columns.get(2));
			});

        } catch (ClientException e) {
            System.err.println(e.getMessage());
        }
    }

    private static ClientConfiguration externalAppCfg() {
        ClientConfiguration cfg = new ClientConfiguration().
            setAddresses("localhost:10800").
            setAffinityAwarenessEnabled(true);

        return cfg;
    }

    /**
     * Use this configuration if the application is supposed to be containarized and deployed in K8.
     *
     * @return ClientConfiguration - a configuration for the app.
     */
    private static ClientConfiguration containarizedAppCfg() {
        KubernetesConnectionConfiguration kcfg = new KubernetesConnectionConfiguration();
        kcfg.setNamespace("ignite-namespace");
        kcfg.setServiceName("ignite-service");

        ClientConfiguration cfg = new ClientConfiguration();
        cfg.setAddressesFinder(new ThinClientKubernetesAddressFinder(kcfg));
        cfg.setAffinityAwarenessEnabled(true);

        return cfg;
    }
}
