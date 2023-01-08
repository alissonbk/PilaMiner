package com.alissonbk.pilacoin;

import com.alissonbk.pilacoin.configuration.ApiConfiguration;
import org.springframework.boot.SpringApplication;;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ApiConfiguration.class)
public class PilacoinApplication {

	public static void main(String[] args) {
		SpringApplication.run(PilacoinApplication.class, args);
	}



}
