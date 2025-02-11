package com.example.powerplant.payload.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SearchResponse {
    private Long averageCapacity;
    private Long totalCapacity;
    private String powerPlants;

}
