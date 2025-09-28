package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@EnableCaching
@EnableScheduling
@SpringBootApplication
public class MyBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyBackendApplication.class, args);

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		String rawPassword = "1234"; // 해시할 비밀번호
		String encodedPassword = encoder.encode(rawPassword);

		System.out.println("Encoded Password: " + encodedPassword);
	}

}
