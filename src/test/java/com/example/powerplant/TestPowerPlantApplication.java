package com.example.powerplant;

import org.springframework.boot.SpringApplication;

public class TestPowerPlantApplication {

    public static void main(String[] args) {
        SpringApplication.from(PowerPlantApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
