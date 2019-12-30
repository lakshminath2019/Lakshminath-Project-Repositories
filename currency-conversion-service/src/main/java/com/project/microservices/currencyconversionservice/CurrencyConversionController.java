package com.project.microservices.currencyconversionservice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CurrencyConversionController {
	
	@Autowired
	CurrencyExchangeServiceProxy currencyExchangeServiceProxy;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//localhost:8100/currency-converter/from/USD/to/INR/quantity/100
	@GetMapping("/currency-converter/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrency(
			@PathVariable String from, @PathVariable String to, @PathVariable BigDecimal quantity)
	{
		Map<String, String> uriVariables = new HashMap<>();
		uriVariables.put("from", from);
		uriVariables.put("to", to);
		
		ResponseEntity<CurrencyConversionBean> responseEntity = new RestTemplate().getForEntity
				("http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyConversionBean.class, uriVariables);
		CurrencyConversionBean response = responseEntity.getBody();
		return new CurrencyConversionBean(response.getId(), from, to, response.getConversionMultiple(),
				quantity, quantity.multiply(new BigDecimal(response.getConversionMultiple())), response.getPort());
	}
	
	@GetMapping("/currency-converter-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrencyFeign(
			@PathVariable String from, @PathVariable String to, @PathVariable BigDecimal quantity)
	{
		//Feign - Problem1
		Map<String, String> uriVariables = new HashMap<>();
		uriVariables.put("from", from);
		uriVariables.put("to", to);
		
		CurrencyConversionBean response = currencyExchangeServiceProxy.retreiveExchangeValue(from, to);
		
		logger.info("{}", response);
		
		return new CurrencyConversionBean(response.getId(), from, to, response.getConversionMultiple(),
				quantity, quantity.multiply(new BigDecimal(response.getConversionMultiple())), response.getPort());
		
	}
}
