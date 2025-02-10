package com.example.powerplant.service;

import com.example.powerplant.entity.PowerPlantEntity;
import com.example.powerplant.payload.request.PowerPlantRegistrationRequest;
import com.example.powerplant.payload.response.PowerPlantRegisterResponse;
import com.example.powerplant.repository.PowerPlantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PowerPlantServiceTest {

    @Mock
    private PowerPlantRepository powerPlantRepository;

    @InjectMocks
    private PowerPlantService powerPlantService;

    @Mock
    private PowerPlantEntity powerPlantEntity;

    private PowerPlantRegistrationRequest request;

    @BeforeEach
    void setUp() {
        request = PowerPlantRegistrationRequest.builder()
                .name("Test-1").capacity(500L).postcode(500).build();
        // Set up request properties as needed for tests
    }

    @Test
    public void testRegisterPowerPlantStream_success() {
        // Given
        Instant now = Instant.now();
        PowerPlantEntity powerPlantEntity = PowerPlantEntity.builder()
                .name("Test-1").capacity(500L).postcode(500).build(); // Assume constructor and setters
        when(powerPlantRepository.saveAll(anyList()))
                .thenReturn(Flux.just(powerPlantEntity)); // Mock repository response

        Flux<PowerPlantRegistrationRequest> requests = Flux.just(request); // Mock input stream

        // When
        Flux<PowerPlantRegisterResponse> result = powerPlantService.registerPowerPlantStream(requests);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(Objects::nonNull) // Assert valid response
                .expectComplete()  // Assert complete stream
                .verify();

        verify(powerPlantRepository, times(1)).saveAll(anyList()); // Verify repository interaction
    }

    @Test
    public void testRegisterPowerPlantStream_withError() {
        // Given
        Instant now = Instant.now();
        PowerPlantEntity powerPlantEntity = PowerPlantEntity.builder()
                .name("Test-1").capacity(500L).postcode(500).build();
        when(powerPlantRepository.saveAll(anyList()))
                .thenReturn(Flux.error(new RuntimeException("Error during save")));

        Flux<PowerPlantRegistrationRequest> requests = Flux.just(request); // Mock input stream

        // When
        Flux<PowerPlantRegisterResponse> result = powerPlantService.registerPowerPlantStream(requests);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class) // Assert error response
                .verify();

        verify(powerPlantRepository, times(1)).saveAll(anyList());
    }

    @Test
    public void testRegisterPowerPlants_success() {
        // Given
        PowerPlantEntity powerPlantEntity = PowerPlantEntity.builder()
                .name("Test-1").capacity(500L).postcode(500).build();
        PowerPlantRegisterResponse response = PowerPlantRegisterResponse.builder()
                .name("Test-1").capacity(500L).postcode(500).build();
        when(powerPlantRepository.saveAll(anyList()))
                .thenReturn(Flux.just(powerPlantEntity)); // Mock repository response

        List<PowerPlantEntity> entities = List.of(powerPlantEntity);

        // When
        Flux<PowerPlantRegisterResponse> result = powerPlantService.registerPowerPlants(entities);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(Objects::nonNull) // Assert valid response
                .expectComplete()  // Assert complete stream
                .verify();

        verify(powerPlantRepository, times(1)).saveAll(anyList());
    }

    @Test
    public void testRegisterPowerPlants_withError() {
        // Given
        when(powerPlantRepository.saveAll(anyList()))
                .thenReturn(Flux.error(new RuntimeException("Error during save"))); // Mock error in repository

        List<PowerPlantEntity> entities = List.of(PowerPlantEntity.builder()
                .name("Test-1").capacity(500L).postcode(500).build());

        // When
        Flux<PowerPlantRegisterResponse> result = powerPlantService.registerPowerPlants(entities);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)  // Assert error is propagated
                .verify();

        verify(powerPlantRepository, times(1)).saveAll(anyList());
    }
}
