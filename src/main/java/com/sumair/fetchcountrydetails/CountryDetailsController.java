package com.sumair.fetchcountrydetails;

import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.sumair.fetchcountrydetails.beans.CountryInfo;
import com.sumair.fetchcountrydetails.beans.CurrencyInfo;
import com.sumair.fetchcountrydetails.beans.ExchangeRatesInfo;
import com.sumair.fetchcountrydetails.models.AuthenticationRequest;
import com.sumair.fetchcountrydetails.models.AuthenticationResponse;
import com.sumair.fetchcountrydetails.models.ExRatesInfo;
import com.sumair.fetchcountrydetails.models.ExRatesInfoRepository;
import com.sumair.fetchcountrydetails.utils.JwtUtil;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;


@RestController
public class CountryDetailsController {

	private RestTemplate restTemplate;

	@Autowired
	private Environment env;
	@Autowired
	private ExRatesInfoRepository exRateRepo;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtUtil jwtTokenUtil;
	@Autowired
	private MyUserDetailsService userDetailsService;
	
	private final Bucket bucket;

	
	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
		}
		catch (BadCredentialsException e) {
			throw new Exception("Incorrect username or password", e);
		}
		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
		final String jwt = jwtTokenUtil.generateToken(userDetails);
		return ResponseEntity.ok(new AuthenticationResponse(jwt));
	}
	
	@GetMapping(path = "/fetchCountryDetails/{param}")
	public ResponseEntity<CountryInfo> fetchCountryDetails(@PathVariable(required = true) String param) {

		if (bucket.tryConsume(1)) {
			CountryInfo[] countryList = getCountryDetailJson(param);

			if(countryList == null) {
				return new ResponseEntity<CountryInfo>(HttpStatus.BAD_REQUEST);
			}

			for (Map.Entry<String, CurrencyInfo> curr: countryList[0].getCurrencies().entrySet())
			{
				if(!env.getProperty("TARGET_CURRENCY_CODE").equalsIgnoreCase(curr.getKey())) {
					curr.getValue().setToIDRRate(convertCurrency(curr.getKey(), env.getProperty("TARGET_CURRENCY_CODE")));
				}else {
					curr.getValue().setToIDRRate(1D);
				}
				ExRatesInfo exInfo = new ExRatesInfo();
				exInfo.setToCurrency(curr.getKey());
				exInfo.setFromCurrency("IDR");
				exInfo.setExchangeRate(String.valueOf(curr.getValue().getToIDRRate()));
				exRateRepo.save(exInfo);
			}

			return ResponseEntity.ok(countryList[0]);
		}else {
			return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
		}
	}

	public CountryDetailsController(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
		Bandwidth limit = Bandwidth.classic(30, Refill.greedy(30, Duration.ofMinutes(1)));
		this.bucket = Bucket4j.builder()
				.addLimit(limit)
				.build();
	}

	private CountryInfo[] getCountryDetailJson(String country) {
		String url = "https://restcountries.com/v3.1/name/"+country+"?fields="+env.getProperty("REST_COUNTRIES_FILTER");
		return this.restTemplate.getForObject(url, CountryInfo[].class);
	}

	private double convertCurrency(String fromCurrency, String toCurrency) {
		String url = "http://data.fixer.io/api/latest?access_key="+env.getProperty("FIXERIO_API_KEY");
		ExchangeRatesInfo exRateInfo = this.restTemplate.getForObject(url, ExchangeRatesInfo.class);
		if(exRateInfo != null && exRateInfo.getRates().get(fromCurrency) != null && exRateInfo.getRates().get(toCurrency) != null) {
			if(fromCurrency.equalsIgnoreCase(exRateInfo.getBase())) {
				return exRateInfo.getRates().get(toCurrency);
			}else {
				Double eurToFC = exRateInfo.getRates().get(fromCurrency);
				Double eurToTC = exRateInfo.getRates().get(toCurrency);
				return (eurToTC/eurToFC);
			}
		}
		return 0;
	}
}
