/*
package com.example.powerplant.service;


import com.example.powerplant.entity.PowerPlantEntity;
import com.example.powerplant.payload.response.SearchResponse;
import com.example.powerplant.repository.PowerPlantRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
class SearchServiceTest {

    @Mock
    private PowerPlantRepository powerPlantRepository;

    @InjectMocks
    private SearchService searchService;  // The service to be tested

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks
    }

    @Test
    void testGetNamesAndStatistics() {
        PowerPlantEntity plantEntity1 = PowerPlantEntity.builder().name("Plant1").postcode(123).capacity(1000L).build();
        PowerPlantEntity plantEntity2 = PowerPlantEntity.builder().name("Plant2").postcode(123).capacity(2000L).build();
        PowerPlantEntity plantEntity3 = PowerPlantEntity.builder().name("Plant3").postcode(123).capacity(1500L).build();

        when(powerPlantRepository.findByPostcodeAndCapacityRange(anyInt(), anyInt(), anyLong(), anyLong()))
                .thenReturn(Flux.just(plantEntity1));

        Mono<SearchResponse> result = searchService.getNamesAndStatistics(500L, 3000L, 1000, 2000);

        // Verify the results using StepVerifier
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertNotNull(response);
                    Assertions.assertEquals(1000L, (long) response.getMinCapacity()); // Min capacity
                    Assertions.assertEquals(2000L, response.getMaxCapacity()); // Max capacity
                    Assertions.assertEquals(1500L, response.getAverageCapacity(), 0.01); // Average capacity
                    Assertions.assertEquals(4500L, response.getTotalCapacity()); // Sum of capacities
                    Assertions.assertEquals(3, response.getPowerPlants().size()); // Verify there are 3 names
                    Assertions.assertTrue(response.getPowerPlants().containsAll(List.of("Plant1", "Plant2", "Plant3"))); // Ensure names match
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testGetNamesAndStatistics_RepositoryReturnsNull() {
        when(powerPlantRepository.findByPostcodeAndCapacityRange(1000, 2000, 500L, 3000L))
                .thenReturn(null);  // Simulating null return

        Mono<SearchResponse> result = searchService.getNamesAndStatistics(500L, 3000L, 1000, 2000);

        StepVerifier.create(result)
                .expectError(IllegalStateException.class)  // Expect an error if null was returned
                .verify();
    }

    @Test
    void testGetNamesAndStatistics_EmptyResults() {
        // Mock repository to return an empty flux (no matching power plants)
        when(powerPlantRepository.findByPostcodeAndCapacityRange(1000, 2000, 500L, 3000L))
                .thenReturn(Flux.empty());

        // Execute the method to be tested
        Mono<SearchResponse> result = searchService.getNamesAndStatistics(500L, 3000L, 1000, 2000);

        // Verify the results using StepVerifier
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertNotNull(response);
                    Assertions.assertEquals(Long.MAX_VALUE, response.getMinCapacity()); // No data, so max possible value
                    Assertions.assertEquals(Long.MIN_VALUE, response.getMaxCapacity()); // No data, so min possible value
                    Assertions.assertEquals(0L, response.getAverageCapacity(), 0.01); // Average should be 0
                    Assertions.assertEquals(0L, response.getTotalCapacity()); // Sum should be 0
                    Assertions.assertTrue(response.getPowerPlants().isEmpty()); // No names should be there
                })
                .expectComplete()
                .verify();
    }
}
*/
