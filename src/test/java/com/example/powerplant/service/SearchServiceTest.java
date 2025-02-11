package com.example.powerplant.service;


import com.example.powerplant.entity.PowerPlantEntity;
import com.example.powerplant.repository.PowerPlantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

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
        searchService = new SearchService(powerPlantRepository);
    }

    @Test
    void testGetNamesAndStatistics() {
        // Mock data
        PowerPlantEntity plantEntity1 = PowerPlantEntity.builder().name("Plant1").postcode(123).capacity(1000L).build();
        PowerPlantEntity plantEntity2 = PowerPlantEntity.builder().name("Plant2").postcode(123).capacity(2000L).build();
        PowerPlantEntity plantEntity3 = PowerPlantEntity.builder().name("Plant3").postcode(123).capacity(1500L).build();

        when(powerPlantRepository.findByPostcodeAndCapacityRange(anyInt(), anyInt(), anyLong(), anyLong()))
                .thenReturn(Flux.just(plantEntity1, plantEntity2, plantEntity3));

        // Call the method under test
        Flux<SearchService.StatsAccumulator> resultFlux = searchService.getNamesAndStatistics(100, 300, 12345, 12347);

        // Verify the results
        StepVerifier.create(resultFlux)
                .expectNextMatches(acc -> acc.getCount() == 0 && acc.getTotalCapacity() == 0)
                .expectNextMatches(acc -> acc.getCount() == 1 && acc.getTotalCapacity() == 1000) // First plant
                .expectNextMatches(acc -> acc.getCount() == 2 && acc.getTotalCapacity() == 3000) // First + second plant
                .expectNextMatches(acc -> acc.getCount() == 3 && acc.getTotalCapacity() == 4500) // All three plants
                .verifyComplete();
    }

}
