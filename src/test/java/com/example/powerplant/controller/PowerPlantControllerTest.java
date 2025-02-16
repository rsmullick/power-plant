package com.example.powerplant.controller;

import com.example.powerplant.payload.request.PowerPlantRegistrationRequest;
import com.example.powerplant.payload.response.PowerPlantRegisterResponse;
import com.example.powerplant.payload.response.SearchResponse;
import com.example.powerplant.service.PowerPlantService;
import com.example.powerplant.service.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PowerPlantControllerTest {

    @Mock
    private PowerPlantService powerPlantService;

    @Mock
    private SearchService searchService;

    @InjectMocks
    private PowerPlantController powerPlantController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterResponseFlux() {
        // Arrange
        PowerPlantRegistrationRequest request = PowerPlantRegistrationRequest.builder()
                .name("Test-1").capacity(500L).postcode(500).build();
        PowerPlantRegisterResponse response = PowerPlantRegisterResponse.builder()
                .name("Test-1").capacity(500L).postcode(500).build();

        when(powerPlantService.registerPowerPlantStream(any(Flux.class)))
                .thenReturn(Flux.just(response));

        // Act
        Flux<PowerPlantRegisterResponse> result = powerPlantController.registerResponseFlux(Flux.just(request));

        // Assert
        StepVerifier.create(result)
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void testSearchResponseMono() {
        // Arrange
        Integer startPostcode = 1000;
        Integer endPostcode = 2000;
        Long minCapacity = 100L;
        Long maxCapacity = 500L;

        SearchResponse searchResponse = new SearchResponse();

        when(searchService.getNamesAndStatistics(minCapacity, maxCapacity, startPostcode, endPostcode))
                .thenReturn(Flux.just(searchResponse));

        // Act
        Flux<SearchResponse> result = powerPlantController.searchResponseMono(startPostcode, endPostcode, minCapacity, maxCapacity);

        // Assert
        StepVerifier.create(result)
                .expectNext(searchResponse)
                .verifyComplete();
    }

    @Test
    void testSearchResponseMono_WithoutOptionalParams() {
        // Arrange
        Integer startPostcode = 1000;
        Integer endPostcode = 2000;
        SearchResponse searchResponse = new SearchResponse();

        when(searchService.getNamesAndStatistics(null, null, startPostcode, endPostcode))
                .thenReturn(Flux.just(searchResponse));

        // Act
        Flux<SearchResponse> result = powerPlantController.searchResponseMono(startPostcode, endPostcode, null, null);

        // Assert
        StepVerifier.create(result)
                .expectNext(searchResponse)
                .verifyComplete();
    }
}