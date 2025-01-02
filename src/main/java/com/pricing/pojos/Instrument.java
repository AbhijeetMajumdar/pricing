package com.pricing.pojos;

import com.pricing.types.InstrumentTypes;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class Instrument {
    String mantaId;
    String ticker;
    InstrumentTypes type;
    String cusip;
    String adp;
    String isin;
    double price;
    Date receivedTms;
    List<String> consumers = new ArrayList<>();
}
