package com.pricing.types;

public enum InstrumentTypes {
    equity("equity"), mutualfund("mutualfund"), option("option");

    private final String value;

    InstrumentTypes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    }
