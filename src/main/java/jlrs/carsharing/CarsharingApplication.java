package jlrs.carsharing;

import jlrs.carsharing.config.EnvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CarsharingApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CarsharingApplication.class);
        app.addInitializers(new EnvConfig());
        app.run(args);

        // SpringApplication.run(CarsharingApplication.class, args);
    }

}
