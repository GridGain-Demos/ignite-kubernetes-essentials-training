package com.gridgain.example;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

public class City {
	@QuerySqlField
	private Integer id;
	@QuerySqlField
	private String Name;
	@QuerySqlField
	private String CountryCode;
	@QuerySqlField
	private String District;
	@QuerySqlField
	private Integer Population;

	public City() {
	}

	public City(Integer id, String name, String countryCode, String district, Integer population) {
		this.id = id;
		Name = name;
		CountryCode = countryCode;
		District = district;
		Population = population;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getCountryCode() {
		return CountryCode;
	}

	public void setCountryCode(String countryCode) {
		CountryCode = countryCode;
	}

	public String getDistrict() {
		return District;
	}

	public void setDistrict(String district) {
		District = district;
	}

	public Integer getPopulation() {
		return Population;
	}

	public void setPopulation(Integer population) {
		Population = population;
	}

	@Override
	public String toString() {
		return "City [id=" + id + ", Name=" + Name + ", CountryCode=" + CountryCode + ", District=" + District
				+ ", Population=" + Population + "]";
	}

}
