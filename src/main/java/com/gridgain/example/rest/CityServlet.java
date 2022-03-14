package com.gridgain.example.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.FieldsQueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.IgniteConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gridgain.example.City;
import com.gridgain.example.CityKey;

public class CityServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String QUERY = "SELECT city.name, MAX(city.population), country.name, country.GovernmentForm FROM country "
			+ "JOIN city ON city.countrycode = country.code "
			+ "GROUP BY city.name, country.name, country.GovernmentForm, city.population "
			+ "ORDER BY city.population DESC LIMIT ?";

	private final Ignite ignite;

	public CityServlet(IgniteConfiguration configuration) {
		ignite = Ignition.start(configuration);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("CityServlet::doGet");
		IgniteCache<CityKey, City> cities = ignite.getOrCreateCache("City");

		SqlFieldsQuery query = new SqlFieldsQuery(QUERY).setSchema("PUBLIC").setArgs(10);

		System.out.println("Running Query;");
		System.out.println(QUERY);
		System.out.println();

		FieldsQueryCursor<List<?>> cursor = cities.query(query);

		List<CityRes> res = new ArrayList<>();

		cursor.getAll().forEach(columns -> {
			res.add(new CityRes(columns.get(0).toString(), Integer.parseInt(columns.get(1).toString()),
					columns.get(2).toString()));
			System.out.println(columns.get(0) + ", population = " + columns.get(1) + ", country=" + columns.get(2));
		});

		cursor.close();

		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(res);

		resp.setStatus(HttpStatus.SC_OK);
		resp.setContentType("application/json");
		resp.getOutputStream().print(json);
	}


	public static class CityRes {
		private String city;
		private Integer population;
		private String country;

		public CityRes(String city, Integer population, String country) {
			this.city = city;
			this.population = population;
			this.country = country;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public Integer getPopulation() {
			return population;
		}

		public void setPopulation(Integer population) {
			this.population = population;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

	}

}
