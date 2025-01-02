package com.pricing.service.vendor;


import com.pricing.pojos.Instrument;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class FactsetEquityClient {


    public List<Instrument> getEquityPrices(Message<List<Instrument>> message) {
        List<Instrument> list = message.getPayload();
        for (Instrument ins : list) {
            ins.setPrice(100.f);
            ins.setReceivedTms(new Date());
        }
        System.out.println("Factset " + Thread.currentThread());
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            //throw new RuntimeException(e);
        }
        return list;
    }
}
