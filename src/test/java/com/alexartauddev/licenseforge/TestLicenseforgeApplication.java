package com.alexartauddev.licenseforge;

import org.springframework.boot.SpringApplication;

public class TestLicenseforgeApplication {

	public static void main(String[] args) {
		SpringApplication.from(LicenseforgeApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
