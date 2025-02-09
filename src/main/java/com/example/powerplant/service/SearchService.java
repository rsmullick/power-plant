package com.example.powerplant.service;

import com.example.powerplant.payload.response.PowerPlantRegisterResponse;
import com.example.powerplant.payload.response.SearchResponse;
import com.example.powerplant.repository.PowerPlantRepository;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SearchService {
    private final PowerPlantRepository powerPlantRepository;

    public SearchService(PowerPlantRepository powerPlantRepository) {
        this.powerPlantRepository = powerPlantRepository;
    }
    public Mono<SearchResponse> getPowerPlants(Integer startPostcode,
                                               Integer endPostcode,
                                               Long minCapacity,
                                               Long maxCapacity) {
        // Create the sink that will emit the power plant data to the client
        Sinks.Many<PowerPlantRegisterResponse> powerPlantSink = Sinks.many().multicast().onBackpressureBuffer();
StatsAccumulator accumulator = new StatsAccumulator();
        // Fetch power plants from the repository and process them
        return powerPlantRepository
                .findByPostcodeAndCapacityRange(startPostcode, endPostcode, minCapacity, maxCapacity)
                .map(PowerPlantService::convertToResponse) // Convert to response format
                .doOnNext(powerPlant -> {
                    log.debug("Emitting power plant: {}", powerPlant); // Add logging for debug
                    powerPlantSink.tryEmitNext(powerPlant); // Emit to the sink
                })
                .scan(accumulator, this::accumulateStats) // Track stats during the stream
                .doOnTerminate(() -> {
                    log.info("Stream completed. Finalizing flux.");
                   powerPlantSink.tryEmitComplete(); // Signal the completion of the flux stream
                })
                .doOnError(ex -> {
                    log.error("Error during stream processing: ", ex);
                    powerPlantSink.tryEmitError(ex); // Emit error if any
                }).last()
              //  .collectList() // Collect the list of results to finalize stats
                .map(list -> {
                    log.info("Found {} powerplants", list);
                    // Stats should be accumulated at the end of the stream
                   // StatsAccumulator accumulator = list.isEmpty() ? new StatsAccumulator() : list.getLast();

                    // Return the response with statistics and flux
                    return new SearchResponse(
                            accumulator.getMinCapacity(),
                            accumulator.getMaxCapacity(),
                            accumulator.getAverageCapacity(),
                            accumulator.getSumCapacity(),
                            accumulator.names//powerPlantSink.asFlux() // Provide the Flux stream to client
                    );
                });
    }

    // Accumulate stats while streaming the data
    private StatsAccumulator accumulateStats(StatsAccumulator accumulator, PowerPlantRegisterResponse powerPlant) {
        long currentCapacity = powerPlant.getCapacity();
        accumulator.updateStats(currentCapacity, powerPlant.getName()); // Update stats for each power plant
        return accumulator; // Return the updated accumulator
    }

    // StatsAccumulator class to hold statistics
    @Getter @ToString
    public static class StatsAccumulator {
        private long minCapacity = Long.MAX_VALUE;
        private long maxCapacity = Long.MIN_VALUE;
        private long sumCapacity = 0;
        private long countCapacity = 0;
        private long averageCapacity = 0;
        private final List<String> names = new ArrayList<>();

        // Update the statistics with new capacity value
        public void updateStats(long capacity, String name) {
            this.minCapacity = Math.min(this.minCapacity, capacity);
            this.maxCapacity = Math.max(this.maxCapacity, capacity);
            this.sumCapacity += capacity;
            this.countCapacity++;
            this.averageCapacity = countCapacity == 0 ? 0 : sumCapacity / countCapacity;
            this.names.add(name);
        }
    }

}
