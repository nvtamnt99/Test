package com.shopme.site.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.shopme.common.entity.Menu;
import com.shopme.common.entity.Setting;
import com.shopme.site.menu.MenuServices;
import com.shopme.site.setting.SettingServices;

@Component
@Order(3)
public class CommonFilter implements Filter {
	
	@Autowired
	private MenuServices menuService;
	
	@Autowired
	private SettingServices settingService;	
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String url = httpRequest.getRequestURL().toString();
		
		if (url.endsWith(".css") || url.endsWith(".png") || url.endsWith(".js")) {
			chain.doFilter(httpRequest, response);
			return;
		}
		
		System.out.println("Common Filter invoked.");
		
		List<Setting> generalSettings = settingService.getGeneralSettings();
		for (Setting setting : generalSettings) {
			request.setAttribute(setting.getKey(), setting.getValue());
		}
		
		List<Menu> headerMenuItems = menuService.getHeaderMenuItems();
		request.setAttribute("headerMenuItems", headerMenuItems);

		List<Menu> footerMenuItems = menuService.getFooterMenuItems();
		request.setAttribute("footerMenuItems", footerMenuItems);
		
		chain.doFilter(request, response);
	}

}
