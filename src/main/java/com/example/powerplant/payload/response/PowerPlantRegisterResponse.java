package com.example.powerplant.payload.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PowerPlantRegisterResponse {
    private UUID id;

    private String name;

    private Integer postcode;

    private Long capacity;
}
