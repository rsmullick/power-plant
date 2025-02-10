package com.example.powerplant.service;

import com.example.powerplant.entity.PowerPlantEntity;
import com.example.powerplant.payload.request.PowerPlantRegistrationRequest;
import com.example.powerplant.payload.response.PowerPlantRegisterResponse;
import com.example.powerplant.repository.PowerPlantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class PowerPlantService {
    private final PowerPlantRepository powerPlantRepository;

    private static final int BUFFER_SIZE = 10;

    public PowerPlantService(PowerPlantRepository powerPlantRepository) {
        this.powerPlantRepository = powerPlantRepository;
    }

    public Flux<PowerPlantRegisterResponse> registerPowerPlantStream(Flux<PowerPlantRegistrationRequest> requests) {
        Instant now = Instant.now();
        return
                requests.mapNotNull(o -> convertToEntity(o, now)).buffer(BUFFER_SIZE)
                        .parallel()
                        .flatMap(this::registerPowerPlants).sequential().onTerminateDetach();
    }

    public Flux<PowerPlantRegisterResponse> registerPowerPlants(List<PowerPlantEntity> powerPlantEntities) {
        return powerPlantRepository.saveAll(powerPlantEntities).onErrorContinue((e, i) -> {
            log.error("Error For Item {}, error message {}", i, e.getMessage());
        }).map(PowerPlantService::convertToResponse);
    }

    public Mono<PowerPlantRegisterResponse> registerPowerPlant(PowerPlantRegistrationRequest request) {
        return powerPlantRepository.save(convertToEntity(request, Instant.now())).mapNotNull(PowerPlantService::convertToResponse);
    }

    protected static PowerPlantEntity convertToEntity(PowerPlantRegistrationRequest request, Instant timestamp) {
        return PowerPlantEntity.builder()
                .name(request.getName())
                .capacity(request.getCapacity())
                .postcode(request.getPostcode())
                .createdAt(timestamp)
                .build();
    }

    protected static PowerPlantRegisterResponse convertToResponse(PowerPlantEntity powerPlantEntity) {
        return PowerPlantRegisterResponse.builder()
                .id(powerPlantEntity.getId())
                .postcode(powerPlantEntity.getPostcode())
                .capacity(powerPlantEntity.getCapacity())
                .name(powerPlantEntity.getName())
                .build();
    }


}
