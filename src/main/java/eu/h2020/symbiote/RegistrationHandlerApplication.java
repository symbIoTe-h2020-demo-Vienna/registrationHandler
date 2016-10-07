package eu.h2020.symbiote;

import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
//@EnableEurekaClient
public class RegistrationHandlerApplication {

	public static <T> T createFeignClient(Class<T> client, String baseUrl) {
		return Feign.builder().
				encoder(new GsonEncoder()).decoder(new GsonDecoder()).
				target(client,baseUrl);
	}

	public static void main(String[] args) {
		SpringApplication.run(RegistrationHandlerApplication.class, args);
	}
}
