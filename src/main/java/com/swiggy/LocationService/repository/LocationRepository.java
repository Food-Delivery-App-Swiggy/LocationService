package com.swiggy.LocationService.repository;

import com.swiggy.LocationService.entity.DeliveryLocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<DeliveryLocationEntity , Long> {
}
