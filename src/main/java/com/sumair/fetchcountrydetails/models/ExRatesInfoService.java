package com.sumair.fetchcountrydetails.models;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ExRatesInfoService {

	private static List<ExRatesInfo> exRates = new ArrayList<>();

	public ExRatesInfo save(ExRatesInfo exRate) {
		exRates.add(exRate);
		return exRate;
	}
}
