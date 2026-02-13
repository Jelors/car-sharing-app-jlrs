package jlrs.carsharing.config;

import org.testcontainers.containers.MySQLContainer;

public class CustomContainer extends MySQLContainer<CustomContainer> {
    private static final String DB_IMAGE = "mysql:9.5.0";

    private static CustomContainer container;

    private CustomContainer() {
        super(DB_IMAGE);
    }

    public static synchronized CustomContainer getInstance() {
        if (container == null) {
            container = new CustomContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("TEST_DB_URL", container.getJdbcUrl());
        System.setProperty("TEST_DB_USERNAME", container.getUsername());
        System.setProperty("TEST_DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
        super.stop();
    }
}
