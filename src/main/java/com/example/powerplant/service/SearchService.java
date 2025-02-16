package com.example.powerplant.service;

import com.example.powerplant.payload.response.PowerPlantRegisterResponse;
import com.example.powerplant.payload.response.SearchResponse;
import com.example.powerplant.repository.PowerPlantRepository;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class SearchService {
    private final PowerPlantRepository powerPlantRepository;

    public SearchService(PowerPlantRepository powerPlantRepository) {
        this.powerPlantRepository = powerPlantRepository;
    }

    public Flux<SearchResponse> getNamesAndStatistics(Long minCapacity, Long maxCapacity, Integer minPostcode, Integer maxPostcode) {
        return getStatistics(minCapacity, maxCapacity, minPostcode, maxPostcode).mergeWith(powerPlantRepository
                .findByPostcodeAndCapacityRange(minPostcode, maxPostcode, minCapacity, maxCapacity)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(TimeoutException.class, e -> {
                    log.error("No items received for 5 seconds. Completing the Flux.");
                    return Flux.empty(); // Complete the Flux gracefully
                })).doOnComplete(() -> log.info("Request Completed"));
    }
public Mono<SearchResponse> getStatistics(Long minCapacity, Long maxCapacity, Integer minPostcode, Integer maxPostcode) {
        return powerPlantRepository.findCntMinMaxSumAvgCapacityPostcodeAndCapacityRange(minPostcode, maxPostcode, minCapacity, maxCapacity)
                ; // .map(SearchResponse::new);
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
