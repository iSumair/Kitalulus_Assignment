package com.sumair.fetchcountrydetails.beans;

public class CurrencyInfo {

	private String name;
    private String symbol;
    private Double toIDRRate;
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public Double getToIDRRate() {
		return toIDRRate;
	}
	public void setToIDRRate(Double toIDRRate) {
		this.toIDRRate = toIDRRate;
	} 
}
