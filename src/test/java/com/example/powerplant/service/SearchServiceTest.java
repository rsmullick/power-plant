/*
package com.example.powerplant.service;


import com.example.powerplant.payload.response.PowerPlantRegisterResponse;
import com.example.powerplant.payload.response.SearchResponse;
import com.example.powerplant.repository.PowerPlantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        // Prepare mock data
        PowerPlantRegisterResponse plant1 = PowerPlantRegisterResponse.builder().name( "Plant1").postcode( 123).capacity( 1000L).build();
        PowerPlantRegisterResponse plant2 = PowerPlantRegisterResponse.builder().name( "Plant2").postcode( 123).capacity( 2000L).build();
        PowerPlantRegisterResponse plant3 = PowerPlantRegisterResponse.builder().name( "Plant3").postcode( 123).capacity( 1500L).build();

        // Mock repository to return the flux of power plants
        when(powerPlantRepository.findByPostcodeAndCapacityRange(1000, 2000, 500L, 3000L))
                .thenReturn(Flux.just(plant1, plant2, plant3));

        // Execute the method to be tested
        Mono<SearchResponse> result = searchService.getNamesAndStatistics(500L, 3000L, 1000, 2000);

        // Verify the results using StepVerifier
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(1000L, response.getMinCapacity()); // Min capacity
                    assertEquals(2000L, response.getMaxCapacity()); // Max capacity
                    assertEquals(1500L, response.getAverageCapacity(), 0.01); // Average capacity
                    assertEquals(4500L, response.getSumCapacity()); // Sum of capacities
                    assertEquals(3, response.getNames().size()); // Verify there are 3 names
                    assertTrue(response.getNames().containsAll(List.of("Plant1", "Plant2", "Plant3"))); // Ensure names match
                })
                .expectComplete()
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
                    assertEquals(Long.MAX_VALUE, response.getMinCapacity()); // No data, so max possible value
                    assertEquals(Long.MIN_VALUE, response.getMaxCapacity()); // No data, so min possible value
                    assertEquals(0L, response.getAverageCapacity(), 0.01); // Average should be 0
                    assertEquals(0L, response.getSumCapacity()); // Sum should be 0
                    assertTrue(response.getNames().isEmpty()); // No names should be there
                })
                .expectComplete()
                .verify();
    }
}
*/
