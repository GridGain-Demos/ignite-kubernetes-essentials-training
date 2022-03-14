package com.gridgain.example;

public class CityKey {
	private Integer id;
	private String CountryCode;

	public CityKey(Integer id, String countryCode) {
		this.id = id;
		CountryCode = countryCode;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCountryCode() {
		return CountryCode;
	}

	public void setCountryCode(String countryCode) {
		CountryCode = countryCode;
	}

	@Override
	public String toString() {
		return "City [id=" + id + ", CountryCode=" + CountryCode + "]";
	}

}
