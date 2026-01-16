package jlrs.carsharing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("jlrs.carsharing.mapper")
public class CarsharingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarsharingApplication.class, args);
    }

}
