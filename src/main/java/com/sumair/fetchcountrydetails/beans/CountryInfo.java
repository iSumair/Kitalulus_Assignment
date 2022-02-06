
package com.sumair.fetchcountrydetails.beans;

import java.util.Map;

public class CountryInfo {

    private Name name;
    private Map<String, CurrencyInfo> currencies;
    private Integer population;

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public Map<String, CurrencyInfo> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(Map<String, CurrencyInfo> currencies) {
        this.currencies = currencies;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }
}
