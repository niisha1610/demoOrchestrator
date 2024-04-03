package com.sap.cic.pdp.jsonStructures;

import lombok.Data;

@Data
public class ScheduleData {

    private String orchestrationId;  // Can be null

    public ScheduleData(String orchestrationId) {
        this.orchestrationId = orchestrationId;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        if (orchestrationId != null) {
            builder.append("\"orchestrationId\":\"").append(orchestrationId).append("\"}");
        } else {
            builder.append("\"orchestrationId\":null");
        }
        return builder.toString();
    }
}