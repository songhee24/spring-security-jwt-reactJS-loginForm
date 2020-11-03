package com.example.polls.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


//Enabling CORS for our react app
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    private final long MAX_AGE_SECS = 3600;
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("HEAD", "OPTIONS", "GET", "POST", "PUT", "PATCH", "DELETE")
                .maxAge(MAX_AGE_SECS);
    }

    @Override
    public void addResourceHandlers(
            ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/** ** ")
                .addResourceLocations("/WEB-INF/view/react/build/static/");
        registry.addResourceHandler("/** .js")
                .addResourceLocations("/WEB-INF/view/react/build/");
        registry.addResourceHandler("/** .json")
                .addResourceLocations("/WEB-INF/view/react/build/");
        registry.addResourceHandler("/** .ico")
                .addResourceLocations("/WEB-INF/view/react/build/");
        registry.addResourceHandler("/index.html")
                .addResourceLocations("/WEB-INF/view/react/build/index.html");
    }
}
