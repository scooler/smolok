package smolok.eventbus.client.spring

import org.apache.camel.ProducerTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import smolok.eventbus.client.EventBus

@Configuration
class EventBusConfiguration {

    @Bean
    EventBus eventBus(ProducerTemplate producerTemplate) {
        new EventBus(producerTemplate)
    }

}