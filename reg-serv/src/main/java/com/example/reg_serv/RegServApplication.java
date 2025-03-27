package com.example.reg_serv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class RegServApplication {
	public static void main(String[] args) {
		SpringApplication.run(RegServApplication.class, args);
	}
}




