package com.pricing.service.vendor;

import com.pricing.pojos.Instrument;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class EnrichAndDistributeEquityService {
    public static String ConsumerChannelName = "consumerChannelName";

    public Message<Instrument> transformAndDistributeMessage(Message<Instrument> message) {
        Instrument originalPayload = message.getPayload();

        //Some enrichment logic

        //Find the consumer(s) who are interested for this price Or *
        //Update header and send to Distribution Router,
        Message<Instrument> newMsg = MessageBuilder.withPayload(originalPayload)
                .setHeader(ConsumerChannelName, originalPayload.getConsumers().toString())
                .build();

        return newMsg;
    }
}
