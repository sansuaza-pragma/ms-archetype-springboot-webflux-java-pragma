package com.mercantil.operationsandexecution;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.mercantil.operationsandexecution.crosscutting.constants.CommonConstants.PACKAGE_PROJECT_ROOT;

@SpringBootApplication(scanBasePackages = PACKAGE_PROJECT_ROOT)
public class BankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankingApplication.class, args);
	}
}
