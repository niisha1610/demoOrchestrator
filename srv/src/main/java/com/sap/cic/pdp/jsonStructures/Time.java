package com.sap.cic.pdp.jsonStructures;

import lombok.Data;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static com.sap.cic.pdp.constants.SchedularConstants.DATE_FORMAT;

@Data
public class Time {
    private String format = "YYYY-MM-DD HH:mm Z"; // Constant format

    private String date;

    public Time(Instant instant) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT).withZone(ZoneId.systemDefault());
        this.date= formatter.format(Instant.parse(instant.toString()));
    }

    @Override
    public String toString() {
        if (format == null && date == null) {
            return "null";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("{");
        if (format != null) {
            builder.append("\"format\":\"").append(format).append("\",");
        }
        if (date != null) {
            builder.append("\"date\":\"").append(date).append("\"");
        }
        builder.append("}");
        return builder.toString();
    }

}