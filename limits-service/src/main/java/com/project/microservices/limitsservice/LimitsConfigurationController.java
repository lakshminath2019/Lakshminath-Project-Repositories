package com.project.microservices.limitsservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.project.microservices.limitsservice.bean.LimitConfiguration;

@RestController
public class LimitsConfigurationController 
{
	@Autowired
	Configuration configuration;
	
	@GetMapping("/limits")
	public LimitConfiguration retrieveLimitConfigurations()
	{
		return new LimitConfiguration(configuration.getMaximum(), configuration.getMinimum());
	}
	
	@GetMapping("/fault-tolerance")
	@HystrixCommand(fallbackMethod="fallbackRetrieveConfiguration")
	public LimitConfiguration retrieveConfigurations()
	{
		throw new RuntimeException("Not Available");
	}
	
	public LimitConfiguration fallbackRetrieveConfiguration()
	{
		return new LimitConfiguration(9, 99);
	}
}
