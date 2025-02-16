package com.example.powerplant.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResponse {
    private Long count;
    private Long min;
    private Long max;
    private Long total;
    private Double average;
    private String name;

    public SearchResponse(Long count, Long min, Long max, Long total, Double average) {
        this.count = count;
        this.min = min;
        this.max = max;
        this.total = total;
        this.average = average;
    }

    public SearchResponse(String name) {
        this.name = name;
    }
}
