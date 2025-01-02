package com.pricing.beans;

import com.pricing.pojos.Instrument;
import com.pricing.types.InstrumentTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class MessageSender {

    @Autowired
    private QueueChannel instrumentBatchQueue;


    public void sendMessage(List<String> tics, InstrumentTypes type) {
        List<Instrument> list = new ArrayList<>();
        Random random = new Random();
        for (String st : tics) {
            Instrument ins = new Instrument();
            ins.setTicker(st);
            ins.setType(type);
            //Some random logic to interpret Interest List
            switch ((int) (random.nextInt(3) )) {
                case 0:
                    ins.getConsumers().add("IXS");
                    break;
                case 1:
                    ins.getConsumers().add("*");
                    break;
                default:

                    ins.getConsumers().add("RMCS");
            }
            list.add(ins);
        }

        instrumentBatchQueue.send(MessageBuilder.withPayload(list).build());
        System.out.println("** Message sent: ");
    }
}