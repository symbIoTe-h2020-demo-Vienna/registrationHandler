package eu.h2020.symbiote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
//@EnableEurekaClient
public class RegistrationHandlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RegistrationHandlerApplication.class, args);
	}
}
