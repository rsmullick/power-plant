package com.example.powerplant.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PowerPlantRegistrationRequest {
    @NotEmpty
    private String name;
    @NotNull
    @Min(value = 0, message = "Postcode has to be a non negative Integer")
    private Integer postcode;
    @NotNull
    @Min(value = 0, message = "Capacity has to be a non negative value")
    private Long capacity;
}
