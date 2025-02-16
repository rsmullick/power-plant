package com.example.powerplant.service;


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

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


@SpringBootTest
class SearchServiceTest {

    @Mock
    private PowerPlantRepository powerPlantRepository;

    @InjectMocks
    private SearchService searchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        searchService = new SearchService(powerPlantRepository);
    }

    @Test
    void testGetStatistics_Success() {
        SearchResponse stats = new SearchResponse(10L, 100L, 1000L, 5000L, 500.0);
        when(powerPlantRepository.findCntMinMaxSumAvgCapacityPostcodeAndCapacityRange(anyInt(), anyInt(), anyLong(), anyLong()))
                .thenReturn(Mono.just(stats));
        StepVerifier.create(searchService.getStatistics(100L, 1000L, 1000, 2000))
                .expectNextMatches(response ->
                        response.getCount() == 10L &&
                                response.getMin() == 100L &&
                                response.getMax() == 1000L &&
                                response.getTotal() == 5000L &&
                                response.getAverage() == 500.0
                )
                .verifyComplete();
    }

    @Test
    void testGetNamesAndStatistics() {
        // Mock data
        SearchResponse searchResponse1 = new SearchResponse("Plant1");
        SearchResponse searchResponse2 =  new SearchResponse("Plant2");
        SearchResponse searchResponse3 =  new SearchResponse("Plant3");
        SearchResponse searchResponse = new SearchResponse(3L, 1000L, 2000L, 4500L, 1500.00);
        when(powerPlantRepository.findCntMinMaxSumAvgCapacityPostcodeAndCapacityRange(anyInt(), anyInt(), anyLong(), anyLong()))
                .thenReturn(Mono.just(searchResponse));
        when(powerPlantRepository.findByPostcodeAndCapacityRange(anyInt(), anyInt(), anyLong(), anyLong()))
                .thenReturn(Flux.just(searchResponse1, searchResponse2, searchResponse3));
        Flux<SearchResponse> resultFlux = searchService.getNamesAndStatistics(100L, 3000L, 12345, 12347);
        StepVerifier.create(resultFlux)
                .expectNextMatches(acc -> acc.getCount() == 3 && acc.getTotal() == 4500)
                .expectNextMatches(acc -> acc.getName().equals("Plant1"))
                .expectNextMatches(acc -> acc.getName().equals("Plant2"))
                .expectNextMatches(acc -> acc.getName().equals("Plant3"))
                .verifyComplete();
    }

}
