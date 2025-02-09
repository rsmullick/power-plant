package com.example.powerplant.repository;

import com.example.powerplant.entity.PowerPlantEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface PowerPlantRepository extends R2dbcRepository<PowerPlantEntity, UUID> {
    @Query("SELECT * FROM power_plant WHERE postcode >= :startPostcode AND postcode <= :endPostcode AND is_deleted = false AND ((:minCapacity is null) or capacity >= :minCapacity) AND ((:maxCapacity is null) or capacity <= :maxCapacity) ORDER BY capacity")
    Flux<PowerPlantEntity> findByPostcodeAndCapacityRange(@Param("startPostcode") int startPostcode,
                                                          @Param("endPostcode") int endPostcode,
                                                          @Param("minCapacity") Long minCapacity,
                                                          @Param("maxCapacity") Long maxCapacity);

}
