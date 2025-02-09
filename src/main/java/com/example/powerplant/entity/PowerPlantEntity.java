package com.example.powerplant.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Builder
@Getter
@Setter
@Table(name = "power_plant")
public class PowerPlantEntity {

    @Id
    private UUID id;


    private String name;


    private Integer postcode;


    private Long capacity;


    private boolean isDeleted;


    private Instant createdAt;


    private Instant updatedAt;
}
