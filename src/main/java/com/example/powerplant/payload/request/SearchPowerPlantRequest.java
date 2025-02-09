package com.example.powerplant.payload.request;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SearchPowerPlantRequest {
    @Min(value = 0, message = "Postcode has to be a non negative Integer")
    int start;
    @Min(value = 0, message = "Postcode has to be a non negative Integer")
    int end;
    boolean getMin;
}
