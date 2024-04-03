/*
 * ************************************************************************
 *  (C) 2024 SAP SE or an SAP affiliate company. All rights reserved. *
 * ************************************************************************
 */

package com.sap.cic.pdp.handlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import cds.gen.orchestratorservice.DatabricksJobsEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.sap.cds.Result;
import com.sap.cds.ql.cqn.CqnInsert;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cds.services.request.UserInfo;
import com.sap.cic.pdp.autowired.IdempotencyTokenGenerator;
import com.sap.cic.pdp.jsonStructures.DatabricksNewClusterJsonStr;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpResponse.BodySubscriber;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

@ExtendWith(MockitoExtension.class)
class SparkSubmitJobHandlerTest {

  @Mock
  private HttpClient httpClient;

  @Mock
  private HttpResponse<String> httpResponse;

  ObjectWriter objectWriter;

  ObjectMapper objectMapper;

  @Mock
  private OAuth2AuthorizedClientManager authorizedClientServiceAndManager;

  @Mock
  private OAuth2AuthorizedClient oAuth2AuthorizedClient;
  private OAuth2AccessToken oAuth2AccessToken;

  private IdempotencyTokenGenerator idempotencyTokenGenerator;

  @InjectMocks
  @Spy
  private SparkSubmitJobHandler sparkSubmitJobHandler;
  @Mock
  HttpRequest httpRequest;
  @Mock
  private MessageSource messageSource;
  @Mock
  private TaskScheduler taskScheduler;

  @Mock
  private ScheduledFuture<?> scheduledFuture;

  @Mock
  private PersistenceService persistenceService;

  @Mock
  private UserInfo userInfo;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // Mocking dependencies and necessary objects
//    sparkSubmitJobHandler = new SparkSubmitJobHandler(
//        null, messageSource);
    // Http client mocking
//    sparkSubmitJobHandler.client = httpClient;
    idempotencyTokenGenerator = new IdempotencyTokenGenerator();
//    oAuth2AccessToken = mock(OAuth2AccessToken.class);
//    sparkSubmitJobHandler.authorizedClientManager = authorizedClientServiceAndManager;
    sparkSubmitJobHandler.idempotencyTokenGenerator = idempotencyTokenGenerator;
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void createAuthenticatedRequestBuilder() {
    URI uri = URI.create("http://example.com");
    when(authorizedClientServiceAndManager.authorize(any())).thenReturn(oAuth2AuthorizedClient);
    // Mock the behavior of the authorizedClient to return a non-null access token
    OAuth2AccessToken accessToken = mock(OAuth2AccessToken.class);
    when(accessToken.getTokenValue()).thenReturn("your_access_token_value");
    when(oAuth2AuthorizedClient.getAccessToken()).thenReturn(accessToken);
    HttpRequest.Builder requestBuilder = sparkSubmitJobHandler.createAuthenticatedRequestBuilder(
        uri);

    // Assertions
    verify(oAuth2AuthorizedClient).getAccessToken();
    verify(authorizedClientServiceAndManager).authorize(any());

  }

  @Test
  void createJob() throws IOException, InterruptedException, ExecutionException {

    // variables for create job
    HttpRequest.Builder requestBuilder = mock(HttpRequest.Builder.class);
    long jobId = 12345L;
    String responseBody = "{\"job_id\": 12345}";
    String createJson = "{\"jobName\":\"testJob\",\"newCluster\":{\"sparkVersion\":\"7.3.x-scala2.12\",\"nodeTypeId\":\"Standard_DS3_v2\",\"numWorkers\":3,\"sparkConf\":{\"spark.databricks.delta.preview.enabled\":\"true\",\"fs.azure.account.key\":\"WriSbO+Z19nuAupI+qWIQDCnaWv2UbAwdUmIdv4owH/8GbUsegemJLE6xcnpt/HPwK029Kkm1fdF+AStvXcWxw==\"}},\"libraries\":[{\"jar\":\"testJar1\"},{\"jar\":\"testJar2\"}],\"sparkJarTask\":{\"className\":\"TestClassName\",\"parameters\":[\"param1\",\"param2\"]}}";
    URI uri = URI.create("http://example.com");

//    when(authorizedClientServiceAndManager.authorize(any())).thenReturn(oAuth2AuthorizedClient);
    // Mock the behavior of the authorizedClient to return a non-null access token
    OAuth2AccessToken accessToken = mock(OAuth2AccessToken.class);
    when(accessToken.getTokenValue()).thenReturn("your_access_token_value");

    when(authorizedClientServiceAndManager.authorize(any())).thenReturn(oAuth2AuthorizedClient);
    when(authorizedClientServiceAndManager.authorize(any())).thenReturn(oAuth2AuthorizedClient);
    when(accessToken.getTokenValue()).thenReturn("your_access_token_value");
    when(oAuth2AuthorizedClient.getAccessToken()).thenReturn(accessToken);

    Mockito.lenient().when(requestBuilder.method(eq("POST"), any(HttpRequest.BodyPublisher.class)))
        .thenReturn(requestBuilder);
    Mockito.lenient().when(requestBuilder.build()).thenReturn(httpRequest);
    when(httpClient.send(any(HttpRequest.class),
        eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(httpResponse);

    objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    objectMapper = new ObjectMapper();
    when(httpResponse.body()).thenReturn(responseBody);

    //Call the method to be tested
    long result = sparkSubmitJobHandler.createJob(objectWriter, objectMapper, "testJob",
        List.of("testJar1", "testJar2"), "TestClassName", "kafka.json");

    // Assertions
    verify(httpClient).send(any(HttpRequest.class),
        any(HttpResponse.BodyHandler.class));
    assertNotNull(httpResponse.body());
    assertEquals(responseBody, httpResponse.body());
    assertEquals(result, jobId);

  }

  @Test
  void runJob() throws IOException, InterruptedException {
    // variables for create job
    HttpRequest.Builder requestBuilder = mock(HttpRequest.Builder.class);
    long jobId = 12345L;
    long runIdAct = 123456;
    String responseBody = "{\"run_id\": 123456}";
    String createJson = "{\"jobName\":\"testJob\",\"newCluster\":{\"sparkVersion\":\"7.3.x-scala2.12\",\"nodeTypeId\":\"Standard_DS3_v2\",\"numWorkers\":3,\"sparkConf\":{\"spark.databricks.delta.preview.enabled\":\"true\",\"fs.azure.account.key\":\"WriSbO+Z19nuAupI+qWIQDCnaWv2UbAwdUmIdv4owH/8GbUsegemJLE6xcnpt/HPwK029Kkm1fdF+AStvXcWxw==\"}},\"libraries\":[{\"jar\":\"testJar1\"},{\"jar\":\"testJar2\"}],\"sparkJarTask\":{\"className\":\"TestClassName\",\"parameters\":[\"param1\",\"param2\"]}}";
    URI uri = URI.create("http://example.com");

    // Mock the behavior of the authorizedClient to return a non-null access token
    OAuth2AccessToken accessToken = mock(OAuth2AccessToken.class);
    when(accessToken.getTokenValue()).thenReturn("your_access_token_value");

    when(authorizedClientServiceAndManager.authorize(any())).thenReturn(oAuth2AuthorizedClient);
    when(authorizedClientServiceAndManager.authorize(any())).thenReturn(oAuth2AuthorizedClient);
    when(accessToken.getTokenValue()).thenReturn("your_access_token_value");
    when(oAuth2AuthorizedClient.getAccessToken()).thenReturn(accessToken);

    Mockito.lenient().when(requestBuilder.method(eq("POST"), any(HttpRequest.BodyPublisher.class)))
        .thenReturn(requestBuilder);
    Mockito.lenient().when(requestBuilder.build()).thenReturn(httpRequest);
    when(httpClient.send(any(HttpRequest.class),
        eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(httpResponse);

    objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    objectMapper = new ObjectMapper();
    when(httpResponse.body()).thenReturn(responseBody);

    //Call the method to be tested
    long runIdExp = sparkSubmitJobHandler.runJob(objectWriter, jobId, objectMapper);
    // Assertions
    verify(httpClient).send(any(HttpRequest.class),
        any(HttpResponse.BodyHandler.class)); // Verify that HttpClient.sendAsync is called
    assertNotNull(httpResponse.body());
    assertEquals(responseBody, httpResponse.body());
    assertEquals(runIdExp, runIdAct);

  }

  @Test
  void readingJobRunningStatus() throws IOException, InterruptedException {

//    HttpRequest.Builder requestBuilder = mock(HttpRequest.Builder.class);
//    long jobId = 12345L;
//    long runIdAct = 123456;
//    String responseBody = "{\"state\":{\"life_cycle_state\":\"RUNNING\",\"result_state\":\"\"}}";;
//    String createJson = "{\"jobName\":\"testJob\",\"newCluster\":{\"sparkVersion\":\"7.3.x-scala2.12\",\"nodeTypeId\":\"Standard_DS3_v2\",\"numWorkers\":3,\"sparkConf\":{\"spark.databricks.delta.preview.enabled\":\"true\",\"fs.azure.account.key\":\"WriSbO+Z19nuAupI+qWIQDCnaWv2UbAwdUmIdv4owH/8GbUsegemJLE6xcnpt/HPwK029Kkm1fdF+AStvXcWxw==\"}},\"libraries\":[{\"jar\":\"testJar1\"},{\"jar\":\"testJar2\"}],\"sparkJarTask\":{\"className\":\"TestClassName\",\"parameters\":[\"param1\",\"param2\"]}}";
//    URI uri = URI.create("http://example.com");
//
//    // Mock the behavior of the authorizedClient to return a non-null access token
//    OAuth2AccessToken accessToken = mock(OAuth2AccessToken.class);
//    when(accessToken.getTokenValue()).thenReturn("your_access_token_value");
//
//    when(authorizedClientServiceAndManager.authorize(any())).thenReturn(oAuth2AuthorizedClient);
//    when(authorizedClientServiceAndManager.authorize(any())).thenReturn(oAuth2AuthorizedClient);
//    when(accessToken.getTokenValue()).thenReturn("your_access_token_value");
//    when(oAuth2AuthorizedClient.getAccessToken()).thenReturn(accessToken);
//
//
////    Mockito.lenient().when(requestBuilder.method("GET", HttpRequest.BodyPublishers.noBody()))
////        .thenReturn(requestBuilder);
//    when(requestBuilder.method("GET", HttpRequest.BodyPublishers.noBody())).thenReturn(requestBuilder);
////    Mockito.lenient().when(requestBuilder.build()).thenReturn(httpRequest);
//    when(requestBuilder.build()).thenReturn(httpRequest);
//        when(httpClient.send(any(HttpRequest.class),
//        eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(httpResponse);
////    when(httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString()))
////        .thenReturn(httpResponse);
////    when(httpClient.send(any(HttpRequest.class),
////        eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(httpResponse);
//    // Mock behavior of HttpClient
////    when(httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString()))
////        .thenReturn(httpResponse);
//
//    // Mock behavior of HttpRequest.Builder
//////    HttpRequest httpRequest = mock(HttpRequest.class);
////    when(requestBuilder.method("GET", HttpRequest.BodyPublishers.noBody())).thenReturn(requestBuilder);
////    when(requestBuilder.build()).thenReturn(httpRequest); // Properly stubbed here
////
////    // Mock behavior of HttpResponse
////    when(httpResponse.body()).thenReturn(responseBody);
//
//    objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
//    objectMapper = new ObjectMapper();
//    when(httpResponse.body()).thenReturn(responseBody);
//
//    String jobRunningStatus = sparkSubmitJobHandler.readingJobRunningStatus(runIdAct, objectMapper);

  }

  @Test
  void GetUniqueId(){
    String uniID = sparkSubmitJobHandler.GetUniqueId("Testing");
    // Assertions
    assertNotNull(uniID);
  }
  @Test
  void scheduleJob() {

    boolean schedularFlagRunningFlag = true;
    String jobRunningStatus = "PENDING";

//    when(taskScheduler.schedule(any(Runnable.class), any(CronTrigger.class)))
//        .thenReturn(scheduledFuture);
  }

   @Test
   void buildNewCluster() {
     DatabricksNewClusterJsonStr newCluster = sparkSubmitJobHandler.buildNewCluster();
     // Assertions
     assertNotNull(newCluster);
   }

  @Test
  void setJobRunningStatus_schedularStart() {

    boolean schedularFlagRunningFlag = true;
    String jobRunningStatus = "PENDING";

    // set scheduled future to null
    sparkSubmitJobHandler.setScheduledFuture(null);

    // Call the method under test
    sparkSubmitJobHandler.setJobRunningStatus(schedularFlagRunningFlag, jobRunningStatus);

    // Verify that the TaskScheduler's schedule method is called with the appropriate arguments
    verify(taskScheduler).schedule(any(Runnable.class), any(CronTrigger.class));
  }

  @Test
  void setJobRunningStatus_schedularStop() {

    boolean schedularFlagRunningFlag = false;
    String jobRunningStatus = "COMPLETED";

    // set scheduled future to null
    sparkSubmitJobHandler.setScheduledFuture(scheduledFuture);

    sparkSubmitJobHandler.setLatch(new CountDownLatch(1));

    // Mock the message source to return a specific message
    when(messageSource.getMessage(any(String.class), any(), any()))
        .thenReturn("Scheduler job stopped: {0}");
    // Call the method under test
    sparkSubmitJobHandler.setJobRunningStatus(schedularFlagRunningFlag, jobRunningStatus);

    // Verify that the TaskScheduler's schedule method is called with the appropriate arguments
    verify(scheduledFuture).cancel(false);
  }


  @Test
  void lifeCycleStateCheck() {
    boolean lifeCycleStateTrue = sparkSubmitJobHandler.lifeCycleStateCheck("RUNNING");
    boolean lifeCycleStateFalse = sparkSubmitJobHandler.lifeCycleStateCheck("XXXX");

    //Assertions
    assertNotNull(lifeCycleStateTrue);
    assertEquals(true,lifeCycleStateTrue);
    assertNotNull(lifeCycleStateFalse);
    assertEquals(false,lifeCycleStateFalse);
  }

  @Test
  void resultStateCheck() {
    boolean resultStateTrue = sparkSubmitJobHandler.resultStateCheck("SUCCESS");
    boolean resultStateFalse = sparkSubmitJobHandler.resultStateCheck("XXXX");

    //Assertions
    assertNotNull(resultStateTrue);
    assertEquals(true,resultStateTrue);
    assertNotNull(resultStateFalse);
    assertEquals(false,resultStateFalse);
  }

  @Test
  public void insertDataBricksJobsEntityData() {

    // Mock the ScheduleJobContext
//    ScheduleJobContext context = mock(ScheduleJobContext.class);
//    when(context.getOrchestrationId()).thenReturn("orchestrationId");

    // Mock the UserInfo
//    when(userInfo.getName()).thenReturn("user");

    // Mock the current date
//    Date currentDate = new Date();

    // Mock the Result of the insert operation
//    Result result = mock(Result.class);
//    when(persistenceService.run(any(CqnInsert.class))).thenReturn(result);

    // Call the method under test
//    DatabricksJobsEntity databricksJobsEntity = sparkSubmitJobHandler.insertDataBricksJobsEntityData(context.getOrchestrationId(),
//        "jobName", 123L, 456L, "RUNNING");

    //Assertions
//    verify(persistenceService).run(any(CqnInsert.class));
//    verify(persistenceService).run((CqnInsert) any());
//    assertNotNull(databricksJobsEntity);

  }
}