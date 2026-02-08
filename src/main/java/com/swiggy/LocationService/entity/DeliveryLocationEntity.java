package com.swiggy.LocationService.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryLocationEntity {
    @Id
    private Long orderId;
    private Double latitude;
    private Double longitude;
    private Instant timeStamp;

}
