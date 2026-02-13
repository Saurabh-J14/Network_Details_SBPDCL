package com.techLabs.nbpdcl.adapters;

import com.techLabs.nbpdcl.models.device.SpotLoad;

public class DisplayItem {
    private final SpotLoad.Output.CustomerData customerData;
    private final int phase;

    public DisplayItem(SpotLoad.Output.CustomerData customerData, int phase) {
        this.customerData = customerData;
        this.phase = phase;
    }

    public SpotLoad.Output.CustomerData getCustomerData() {
        return customerData;
    }

    public int getPhase() {
        return phase;
    }
}
