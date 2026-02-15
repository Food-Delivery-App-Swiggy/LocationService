package com.swiggy.LocationService.listner;

import com.swiggy.LocationService.entity.DeliveryLocationEntity;
import com.swiggy.LocationService.event.DeliveryLocationEvent;
import com.swiggy.LocationService.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class DeliveryLocationListner {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private     LocationRepository locationRepository;

    @KafkaListener(
            topics = "delivery-location-event",
            groupId = "location-service",
            containerFactory = "deliveryLocationKafkaListenerFactory"
    )
    public void saveLocation(DeliveryLocationEvent event) {

        locationRepository.save(
                new DeliveryLocationEntity(
                        event.getOrderId(),
                        event.getLat(),
                        event.getLng(),
                        event.getTimeStamp()
                )
        );
    }
}
