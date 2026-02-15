package com.swiggy.LocationService.config;


import com.swiggy.LocationService.event.DeliveryLocationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

@Configuration
public class DeliveryKafkaConfig {



    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DeliveryLocationEvent> deliveryLocationKafkaListenerFactory(
            ConsumerFactory<String, DeliveryLocationEvent> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, DeliveryLocationEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        return factory;
    }


}

