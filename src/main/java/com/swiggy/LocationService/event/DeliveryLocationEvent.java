package com.swiggy.LocationService.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryLocationEvent {
    private Long orderId;
    private double lat;
    private double lng;
    private Instant timeStamp;
}
