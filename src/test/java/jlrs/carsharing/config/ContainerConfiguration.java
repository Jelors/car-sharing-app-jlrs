package jlrs.carsharing.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.MySQLContainer;

import javax.sql.DataSource;

@Configuration
public class ContainerConfiguration {
    @Bean(initMethod = "start", destroyMethod = "stop")
    public MySQLContainer<?> mySQLContainer() {
        return new MySQLContainer<>("mysql:8.0.40")
                .withDatabaseName("test")
                .withUsername("test")
                .withPassword("test");
    }

    @Bean
    public DataSource dataSource(MySQLContainer<?> mySQLContainer) {
        var hikariDataSource = new HikariDataSource();

        hikariDataSource.setJdbcUrl(mySQLContainer.getJdbcUrl());
        hikariDataSource.setUsername(mySQLContainer.getUsername());
        hikariDataSource.setPassword(mySQLContainer.getPassword());
        hikariDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        return hikariDataSource;
    }
}
