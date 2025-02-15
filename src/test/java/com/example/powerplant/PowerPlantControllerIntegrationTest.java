package com.example.powerplant;

import com.example.powerplant.payload.request.PowerPlantRegistrationRequest;
import com.example.powerplant.payload.response.PowerPlantRegisterResponse;
import com.example.powerplant.service.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Testcontainers
@Import(TestcontainersConfiguration.class)
public class PowerPlantControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:17.2")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:postgresql://" +
                postgreSQLContainer.getHost() + ":" + postgreSQLContainer.getFirstMappedPort() +
                "/" + postgreSQLContainer.getDatabaseName());
        registry.add("spring.r2dbc.username", postgreSQLContainer::getUsername);
        registry.add("spring.r2dbc.password", postgreSQLContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        // Initialize the database with test data if needed
    }

    @Test
    void testRegisterPowerPlants() {
        PowerPlantRegistrationRequest request1 = PowerPlantRegistrationRequest.builder().name("Plant1").postcode(12345).capacity(100L).build();
        PowerPlantRegistrationRequest request2 = PowerPlantRegistrationRequest.builder().name("Plant2").postcode(67890).capacity(200L).build();

        webTestClient.post()
                .uri("/api/power-plant/register")
                .body(Flux.just(request1, request2), PowerPlantRegistrationRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PowerPlantRegisterResponse.class)
                .hasSize(2);
    }

    @Test
    void testSearchPowerPlants() {
        // Insert test data into the database
        // ...

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/power-plant/search")
                        .queryParam("startPostcode", 10000)
                        .queryParam("endPostcode", 20000)
                        .queryParam("minCapacity", 50L)
                        .queryParam("maxCapacity", 300L)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SearchService.StatsAccumulator.class)
                .hasSize(1); // Adjust based on your test data
    }
}