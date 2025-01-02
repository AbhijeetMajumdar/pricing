//package com.pricing.service.vendor;
//
//import com.pricing.pojos.Instrument;
//import org.springframework.integration.annotation.Filter;
//import org.springframework.messaging.Message;
//import org.springframework.stereotype.Component;
//
//@Component
//public class ConditionalSubscribers {
//    @Filter(inputChannel = "latestPricePubSubChannel", outputChannel = "ixsQueue")
//    public boolean filterForQueueA(Message<Instrument> message) {
//        // Condition: Process only if header 'type' is 'A'
//        String headerVal = (String) message.getHeaders().
//                get(EnrichAndDistributeService.ConsumerChannelName);
//        return "*".equals(headerVal) || "ixs".equals(headerVal);
//    }
//
////    @Filter(inputChannel = "latestPricePubSubChannel", outputChannel = "rmcsQueue")
//    public boolean filterForQueueB(Message<Instrument> message) {
//        // Condition: Process only if header 'type' is 'B'
//        String headerVal = (String) message.getHeaders().
//                get(EnrichAndDistributeService.ConsumerChannelName);
//        return "*".equals(headerVal) || "rmcs".equals(headerVal);
//    }
//}
