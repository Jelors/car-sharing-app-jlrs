package jlrs.carsharing;

import jlrs.carsharing.config.EnvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CarsharingApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CarsharingApplication.class);
        app.addInitializers(new EnvConfig());
        app.run(args);

        // SpringApplication.run(CarsharingApplication.class, args);
    }

}
