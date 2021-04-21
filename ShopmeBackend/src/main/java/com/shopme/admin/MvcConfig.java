package com.shopme.admin;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("index");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		exposeDirectory("../category-images", registry);
		exposeDirectory("user-photos", registry);
		exposeDirectory("../brand-logos", registry);
		exposeDirectory("../product-images", registry);
		exposeDirectory("../site-logo", registry);
	}
	
	private void exposeDirectory(String dirName, ResourceHandlerRegistry registry) {
		Path uploadDir = Paths.get(dirName);
		String uploadPath = uploadDir.toFile().getAbsolutePath();
		
		if (dirName.startsWith("../")) dirName = dirName.replace("../", "");
		
		registry.addResourceHandler("/" + dirName + "/**").addResourceLocations("file:/"+ uploadPath + "/");		
	}
}
