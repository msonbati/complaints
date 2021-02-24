package com.quality.complaints;

import com.quality.complaints.config.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class ComplaintsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ComplaintsApplication.class, args);
	}

}
