package com.intel.samples.contextsensingapiflow.listener;

public enum MONITOR_INTERVAL {
    
    ONE_SECOND(1000),
    FIVE_SECONDS(5000),
    TEN_SECONDS(10000),
    FIFTEEN_SECONDS(15000),
    TWENTY_SECONDS(20000),
    TWENTY_FIVE_SECONDS(25000),
    THIRTY_SECONDS(30000),
    ONE_MINUTE(60000),
    TWO_MINUTES(120000),
    FIVE_MINUTES(300000),
    TEN_MINUTES(600000);
    
    private final int mInterval;
    
    private MONITOR_INTERVAL(int quantity) {
        mInterval = quantity;
    }
    
    public int getInterval() {
        return mInterval;
    }
}