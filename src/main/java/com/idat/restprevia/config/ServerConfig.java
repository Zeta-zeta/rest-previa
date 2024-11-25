package com.idat.restprevia.config;

import com.idat.restprevia.controller.MoviesController;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerConfig extends ResourceConfig {
    public ServerConfig(){
        register(MoviesController.class);
    }
}
