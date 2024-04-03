package com.sap.cic.pdp.handlers;

import cds.gen.adjustforecastservice.ForecastAdjustments;
import cds.gen.adjustforecastservice.ForecastAdjustmentsContext;
import com.sap.cic.pdp.utils.KafkaWriter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class EventReceiverTest {
    @Mock
    private SparkSubmitJobHandler sparkSubmitJobHandler;
    @Mock
    KafkaWriter kafkaWriter;
    @InjectMocks
    @Spy
    private EventReceiver eventReceiver;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void testEventReceived() throws IOException, InterruptedException {
        // Set up mock data
        ForecastAdjustmentsContext context = mock(ForecastAdjustmentsContext.class);
        ForecastAdjustments event = ForecastAdjustments.create();
        String expectedPath = "/path/to/event.json";

        // Configure mock behavior
        when(context.getData()).thenReturn(event);
        when(kafkaWriter.writeKafkaJson(event.toJson())).thenReturn(expectedPath);

        // Call the method under test
        eventReceiver.eventReceived(context);

        // Verify interactions and results
        verify(sparkSubmitJobHandler).adjustForecast(expectedPath);
    }
}
