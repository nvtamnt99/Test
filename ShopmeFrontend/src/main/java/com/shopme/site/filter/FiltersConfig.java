package com.shopme.site.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class FiltersConfig {

	@Autowired
	private CommonFilter commonFilter;
	
	@Autowired
	private CurrencySettingFilter currencySettingFilter;
	
	@Bean
	public FilterRegistrationBean<CommonFilter> registerCommonFilter() {
		FilterRegistrationBean<CommonFilter> registrationBean = new FilterRegistrationBean<>();
		
		registrationBean.setFilter(commonFilter);
//		registrationBean.setOrder(2);
		registrationBean.addUrlPatterns("/*");
		
		return registrationBean;
	}
	
	@Bean
	public FilterRegistrationBean<CurrencySettingFilter> registerSettingFilter() {
		FilterRegistrationBean<CurrencySettingFilter> registrationBean = new FilterRegistrationBean<>();
		
		registrationBean.setFilter(currencySettingFilter);
//		registrationBean.setOrder(3);
		registrationBean.addUrlPatterns("/c/*", "/p/*", "/cart/*", "/checkout/*", "/orders/*");
		
		return registrationBean;
	}	
}
