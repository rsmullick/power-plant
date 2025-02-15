package com.example.powerplant;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.PostgreSQLR2DBCDatabaseContainerProvider;
import org.testcontainers.r2dbc.R2DBCDatabaseContainer;
import org.testcontainers.r2dbc.R2DBCDatabaseContainerProvider;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>(DockerImageName.parse("postgres:17.2"))
                .withDatabaseName("power_plant")
                .withUsername("testuser")
                .withPassword("testpass");
        container.start();
        return container;
    }


    @Bean
    public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
                new ClassPathResource("power_plant_ddl.sql")
        );
        initializer.setDatabasePopulator(populator);
        return initializer;
    }
}