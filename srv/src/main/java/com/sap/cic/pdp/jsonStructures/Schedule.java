package com.sap.cic.pdp.jsonStructures;

import lombok.Data;

@Data
public class Schedule {

    private String time;       // Can be null
    private String description; // Can be null
    private ScheduleData data;         // Can be null
    private boolean active = true;  // Not null (default)
    private Time startTime;       // Can be null
    private Time endTime;        // Can be null


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        if (time != null) {
            builder.append("\"time\":\"").append(time).append("\",");
        }
        if (description != null) {
            builder.append("\"description\":\"").append(description).append("\",");
        }
        if (data != null) {
            builder.append("\"data\":").append(data).append(",");
        }
        if(startTime != null) {
            builder.append("\"startTime\":").append(startTime).append(",");
        }

        if(endTime != null) {
            builder.append("\"endTime\":").append(endTime).append(",");
        }

        builder.append("\"active\":").append(active).append("}");
        return builder.toString();
    }
}
