package com.pricing.beans;

import com.pricing.types.InstrumentTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class TestRunner implements CommandLineRunner {

    @Autowired
    private MessageSender messageSender;

    @Override
    public void run(String... args) {
        System.out.println("Start processing time " + (System.currentTimeMillis()));

        for (int i =0;i< 1000 ;i++) {
            messageSender.sendMessage(Arrays.asList(
                    "AAPL-" + i, "TSLA-"+i , "MSFT-"+i
                    ,"JPM-" + i, "CTS-"+i , "ABC-"+i
                    ,"RFE-" + i, "AAS-"+i , "XYZ-"+i), InstrumentTypes.equity);
        }

    }
}
