package com.sap.cic.pdp.handlers;

import cds.gen.orchestratorservice.SchedulerEntity;
import com.sap.cds.Result;
import com.sap.cds.ql.cqn.*;
import com.sap.cds.reflect.CdsModel;
import com.sap.cds.services.cds.CdsDeleteEventContext;
import com.sap.cds.services.cds.CdsUpdateEventContext;
import com.sap.cic.pdp.oauthtoken.OAuth2TokenClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mockito.*;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import com.sap.cds.services.persistence.PersistenceService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SchedulerHandlerTest {

    Logger logger = LoggerFactory.getLogger(SchedulerHandlerTest.class);
    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private PersistenceService mockPersistenceService;

    @Mock
    private OAuth2TokenClient mockOAuth2TokenClient;

    @InjectMocks
    private SchedulerHandler schedulerHandler;

    @Mock
    CdsUpdateEventContext updateEventContext;

    @Mock
    CdsDeleteEventContext deleteEventContext;

    @Mock
    CdsModel cdsModel;

    @Mock
    CqnUpdate cqnUpdate;

    @Mock
    CqnDelete cqnDelete;

    @Mock
    CqnStructuredTypeRef cqnStructuredTypeRef;

    @Mock
    CqnAnalyzer cqnAnalyzer;

    @Mock
    AnalysisResult analysisResult;

    @BeforeEach
    public void setUp() {
        schedulerHandler = new SchedulerHandler(); // Assuming constructor is accessible for testing
        MockitoAnnotations.initMocks(this);
    }

    // Sample SchedulerEntity object for testing
    private SchedulerEntity createTestEntity() {
        SchedulerEntity entity = SchedulerEntity.create();
        entity.setSchedulerName("Test Scheduler");
        // Set other required fields
        return entity;
    }

    @Test
    void testCreateSchedule_Success() throws IOException, InterruptedException {
        // Mock successful responses
        HttpResponse mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(201);
        when(mockResponse.body()).thenReturn("{\"_id\": 12345, \"schedules\": [{\"scheduleId\": \"b2f76995-376d-45e2-9951-66b90bdaa7f3\"}]}");
        when(mockHttpClient.send(any(HttpRequest.class), any())).thenReturn(mockResponse);

        // Mock successful token retrieval
        when(mockOAuth2TokenClient.getAccessToken(anyString(), anyString(), anyString(), anyString()))
                .thenReturn("valid_access_token");

        // Create SchedulerEntity
        SchedulerEntity entity = createTestEntity();

        // Call the method under test
        schedulerHandler.createSchedule(null, entity);

        // Assert that schedulerJobID and schedulerID are set
        assertEquals(12345, entity.getSchedulerJobID());
        assertEquals("b2f76995-376d-45e2-9951-66b90bdaa7f3", entity.getSchedulerID());
    }

    @Test
    public void testUpdateSchedule_SuccessfulUpdate() throws IOException, InterruptedException {
        // Mock dependencies (replace with appropriate mocks/stubs)
        try {
        Map<String,Object> rootKeys=new HashMap<>();
        rootKeys.put("ID", "123");
        MockedStatic<CqnAnalyzer> cqnAnalyzer1 = Mockito.mockStatic(CqnAnalyzer.class);
        when(updateEventContext.getModel()).thenReturn(cdsModel);
        when(updateEventContext.getCqn()).thenReturn(cqnUpdate);
        when(updateEventContext.getCqn().ref()).thenReturn(cqnStructuredTypeRef);

        when(CqnAnalyzer.create(cdsModel)).thenReturn(cqnAnalyzer);
        when(CqnAnalyzer.create(cdsModel).analyze(cqnStructuredTypeRef)).thenReturn(analysisResult);
        when(CqnAnalyzer.create(cdsModel).analyze(cqnStructuredTypeRef).rootKeys()).thenReturn(rootKeys);

        // Mock successful persistence service query result
        Mockito.when(mockPersistenceService.run(any(CqnSelect.class))).thenReturn(Mockito.mock(Result.class));
        //Mockito.when(((Result) mockPersistenceService.run(any(CqnSelect.class))).listOf(SchedulerEntity.class)).thenReturn(Collections.singletonList(createTestEntity()));

        Result dbResult=mock(Result.class);
        when(mockPersistenceService.run(any(CqnSelect.class))).thenReturn(dbResult);
        when(dbResult.listOf(SchedulerEntity.class)).thenReturn(Collections.singletonList(createTestEntity()));

        // Simulate successful update API response (consider edge cases)
        String successResponse = "{\"message\": \"Schedule updated successfully\"}";
        HttpResponse mockSuccessResponse = Mockito.mock(HttpResponse.class);
        Mockito.when(mockSuccessResponse.statusCode()).thenReturn(200);
        Mockito.when(mockSuccessResponse.body()).thenReturn(successResponse);
        Mockito.when(mockHttpClient.send(any(HttpRequest.class),any())).thenReturn(mockSuccessResponse);

        // Call the updateSchedule method
        schedulerHandler.updateSchedule(updateEventContext, createTestEntity());
        }catch (Exception exception) {
            logger.error(exception.getMessage());}

    }

    @Test
    public void testDeleteSchedule_SuccessfulUpdate() throws IOException, InterruptedException {
        // Mock dependencies (replace with appropriate mocks/stubs)

        Map<String,Object> rootKeys=new HashMap<>();
        rootKeys.put("ID", "123");
        when(deleteEventContext.getModel()).thenReturn(cdsModel);
        when(deleteEventContext.getCqn()).thenReturn(cqnDelete);
        when(deleteEventContext.getCqn().ref()).thenReturn(cqnStructuredTypeRef);

        when(CqnAnalyzer.create(cdsModel)).thenReturn(cqnAnalyzer);
        when(CqnAnalyzer.create(cdsModel).analyze(cqnStructuredTypeRef)).thenReturn(analysisResult);
        when(CqnAnalyzer.create(cdsModel).analyze(cqnStructuredTypeRef).rootKeys()).thenReturn(rootKeys);

        // Mock successful persistence service query result
        Mockito.when(mockPersistenceService.run(any(CqnSelect.class))).thenReturn(Mockito.mock(Result.class));
        //Mockito.when(((Result) mockPersistenceService.run(any(CqnSelect.class))).listOf(SchedulerEntity.class)).thenReturn(Collections.singletonList(createTestEntity()));

        Result dbResult=mock(Result.class);
        when(mockPersistenceService.run(any(CqnSelect.class))).thenReturn(dbResult);
        when(dbResult.listOf(SchedulerEntity.class)).thenReturn(Collections.singletonList(createTestEntity()));

        // Simulate successful update API response (consider edge cases)
        String successResponse = "{\"message\": \"Schedule updated successfully\"}";
        HttpResponse mockSuccessResponse = Mockito.mock(HttpResponse.class);
        Mockito.when(mockSuccessResponse.statusCode()).thenReturn(200);
        Mockito.when(mockSuccessResponse.body()).thenReturn(successResponse);
        Mockito.when(mockHttpClient.send(any(HttpRequest.class),any())).thenReturn(mockSuccessResponse);

        // Call the deleteSchedule method
        schedulerHandler.deleteSchedule(deleteEventContext, createTestEntity());

    }
}
