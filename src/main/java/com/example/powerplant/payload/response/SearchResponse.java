package com.example.powerplant.payload.response;

import lombok.*;
import reactor.core.publisher.Flux;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SearchResponse {
    private Long minCapacity;
    private Long maxCapacity;
    private Long averageCapacity;
    private Long totalCapacity;
    private List<String> powerPlants;

}
