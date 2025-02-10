package com.example.powerplant.controller;

import com.example.powerplant.payload.request.PowerPlantRegistrationRequest;
import com.example.powerplant.payload.response.PowerPlantRegisterResponse;
import com.example.powerplant.payload.response.SearchResponse;
import com.example.powerplant.service.PowerPlantService;
import com.example.powerplant.service.SearchService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/power-plant")
public class PowerPlantController {
    private final PowerPlantService powerPlantService;
    private final SearchService searchService;

    public PowerPlantController(PowerPlantService powerPlantService, SearchService searchService) {
        this.powerPlantService = powerPlantService;
        this.searchService = searchService;
    }

    @PostMapping("/register")
    public Flux<PowerPlantRegisterResponse> registerResponseFlux(@RequestBody Flux<PowerPlantRegistrationRequest> powerPlants) {
        return powerPlantService.registerPowerPlantStream(powerPlants);
    }

    @GetMapping(value = "/search")
    public Mono<SearchResponse> searchResponseMono(@RequestParam Integer startPostcode,
                                                   @RequestParam Integer endPostcode,
                                                   @RequestParam(required = false) Long minCapacity,
                                                   @RequestParam(required = false) Long maxCapacity) {
        return searchService.getNamesAndStatistics(minCapacity, maxCapacity, startPostcode, endPostcode);
    }


}
