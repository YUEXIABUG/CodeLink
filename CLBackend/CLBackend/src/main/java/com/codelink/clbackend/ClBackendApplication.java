package com.codelink.clbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@MapperScan("com.codelink.clbackend.mapper")
public class ClBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClBackendApplication.class, args);
	}

}
