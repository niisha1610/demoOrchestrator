package com.sap.cic.pdp.handlers;

import cds.gen.adjustforecastservice.AdjustForecastService_;
import cds.gen.adjustforecastservice.ForecastAdjustments;
import cds.gen.adjustforecastservice.ForecastAdjustmentsContext;
import com.sap.cic.pdp.utils.KafkaWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import java.io.IOException;

@Component
@ServiceName(AdjustForecastService_.CDS_NAME)
public class EventReceiver implements EventHandler{
    @Autowired
    SparkSubmitJobHandler sparkSubmitJobHandler;
    @Autowired
    KafkaWriter kafkaWriter;

    @On(service = AdjustForecastService_.CDS_NAME)
    public void eventReceived(ForecastAdjustmentsContext context) throws IOException, InterruptedException {
        ForecastAdjustments event = context.getData();
        System.out.println("-----Event Consumed----- : '" + event.getAdjustmentName() + "'");
        String path = kafkaWriter.writeKafkaJson(event.toJson());
        sparkSubmitJobHandler.adjustForecast(path);
    }
}
