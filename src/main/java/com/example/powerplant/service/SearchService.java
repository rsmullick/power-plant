package com.example.powerplant.service;

import com.example.powerplant.payload.response.PowerPlantRegisterResponse;
import com.example.powerplant.repository.PowerPlantRepository;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class SearchService {
    private final PowerPlantRepository powerPlantRepository;

    public SearchService(PowerPlantRepository powerPlantRepository) {
        this.powerPlantRepository = powerPlantRepository;
    }

    public Flux<StatsAccumulator> getNamesAndStatistics(Long minCapacity, Long maxCapacity, Integer minPostcode, Integer maxPostcode) {
        return powerPlantRepository
                .findByPostcodeAndCapacityRange(minPostcode, maxPostcode, minCapacity, maxCapacity)
                .map(PowerPlantService::convertToResponse) // Convert to response format
                .scan(new StatsAccumulator(), StatsAccumulator::accumulate) // Accumulate statistics
                .map(o -> o)
                .timeout(Duration.ofSeconds(5)) // Timeout after 5 seconds of inactivity
                .onErrorResume(TimeoutException.class, e -> {
                    log.error("No items received for 5 seconds. Completing the Flux.");
                    return Flux.empty(); // Complete the Flux gracefully
                });
    }

    // StatsAccumulator class to hold statistics
    @Getter
    @ToString
    public static
    class StatsAccumulator {
        long count = 0;
        double totalCapacity = 0;
        double averageCapacity = 0;
        String name;

        StatsAccumulator accumulate(PowerPlantRegisterResponse response) {
            this.count++;
            this.totalCapacity += response.getCapacity();
            this.averageCapacity = this.totalCapacity / this.count;
            this.name = response.getName();
            return this;
        }


    }


}
