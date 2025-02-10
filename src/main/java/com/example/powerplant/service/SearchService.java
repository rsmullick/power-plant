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
import reactor.util.function.Tuples;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SearchService {
    private final PowerPlantRepository powerPlantRepository;

    public SearchService(PowerPlantRepository powerPlantRepository) {
        this.powerPlantRepository = powerPlantRepository;
    }

    public Mono<SearchResponse> getNamesAndStatistics(long minCapacity, long maxCapacity, int minPostcode, int maxPostcode) {
        // Create a sink for streaming names
        // Sinks.Many<String> namesSink = Sinks.many().multicast().onBackpressureBuffer();
        StatsAccumulator accumulator = new StatsAccumulator();
        List<String> names = new ArrayList<>();
        // Stream power plants with names and capacities
        Flux<PowerPlantRegisterResponse> powerPlantsFlux = powerPlantRepository
                .findByPostcodeAndCapacityRange(minPostcode, maxPostcode, minCapacity, maxCapacity)
                .map(PowerPlantService::convertToResponse) // Convert to response format
                .doOnNext(powerPlant -> {
                    // Emit names progressively
                    // namesSink.tryEmitNext(powerPlant.getName());
                    //  System.out.println("Name: " + powerPlant.getName());
                    // Accumulate stats incrementally
                    accumulator.updateStats(powerPlant.getCapacity());
                    names.add(powerPlant.getName());
                    System.out.println("Accumulator stats: " + accumulator);
                });

        // Compute the statistics incrementally while streaming
        Mono<Long> totalCapacityMono = powerPlantsFlux.map(PowerPlantRegisterResponse::getCapacity)
                .reduce(0L, Long::sum); // Streaming total capacity

        Mono<Double> averageCapacityMono = powerPlantsFlux.map(PowerPlantRegisterResponse::getCapacity)
                .scan(Tuples.of(0L, 0L), (tuple, capacity) -> Tuples.of(tuple.getT1() + capacity, tuple.getT2() + 1)) // Use Tuple2 to accumulate sum and count
                .last() // Get the last tuple containing the total sum and count
                .map(tuple -> tuple.getT1() / (double) tuple.getT2()); // Compute average

        // Zip the results together, apply timeout to avoid indefinite waiting
        return Mono.zip(

                        totalCapacityMono,
                        averageCapacityMono
                )
                .map(tuple -> new SearchResponse(
                        accumulator.getMinCapacity(),
                        accumulator.getMaxCapacity(),
                        accumulator.getAverageCapacity(),
                        accumulator.getSumCapacity(),
                        names // Provide the list of names
                ));
    }

    // StatsAccumulator class to hold statistics
    @Getter
    @ToString
    public static class StatsAccumulator {
        private long minCapacity = Long.MAX_VALUE;
        private long maxCapacity = Long.MIN_VALUE;
        private long sumCapacity = 0;
        private long countCapacity = 0;
        private long averageCapacity = 0;

        // Update the statistics with new capacity value
        public void updateStats(long capacity) {
            this.minCapacity = Math.min(this.minCapacity, capacity);
            this.maxCapacity = Math.max(this.maxCapacity, capacity);
            this.sumCapacity += capacity;
            this.countCapacity++;
            this.averageCapacity = countCapacity == 0 ? 0 : sumCapacity / countCapacity;
        }
    }


}
