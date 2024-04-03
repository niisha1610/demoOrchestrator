package com.sap.cic.pdp.jsonStructures;

import lombok.Data;

import java.util.List;

@Data // Lombok annotation for getters and setters
public class JobSchedule {

    private String name;        // Can be null
    private String description; // Can be null
    private String action;      // Can be null
    private boolean active = true;  // Not null (default)
    private String httpMethod;  // Can be null
    private Time startTime;       // Can be null
    private Time endTime;        // Can be null
    private List<Schedule> schedules;  // Can be null (may be empty)

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        if (name != null) {
            builder.append("\"name\":\"").append(name).append("\",");
        }
        if (description != null) {
            builder.append("\"description\":\"").append(description).append("\",");
        }
        if (action != null) {
            builder.append("\"action\":\"").append(action).append("\",");
        }
        builder.append("\"active\":").append(active).append(",");
        if (httpMethod != null) {
            builder.append("\"httpMethod\":").append("\"").append(httpMethod).append("\",");
        }
        if(startTime != null) {
            builder.append("\"startTime\":").append(startTime).append(",");
        }
        if(endTime != null) {
            builder.append("\"endTime\":").append(endTime).append(",");
        }
        if (schedules != null && !schedules.isEmpty()) {
            builder.append("\"schedules\":");
            builder.append(schedules);
        }
        builder.append("}");
        return builder.toString();
    }
}
