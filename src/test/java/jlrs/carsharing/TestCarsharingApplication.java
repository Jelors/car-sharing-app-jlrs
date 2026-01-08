package jlrs.carsharing;

import org.springframework.boot.SpringApplication;

public class TestCarsharingApplication {

	public static void main(String[] args) {
		SpringApplication.from(CarsharingApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
