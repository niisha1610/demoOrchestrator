/*
 * ************************************************************************
 *  (C) 2024 SAP SE or an SAP affiliate company. All rights reserved. *
 * ************************************************************************
 */

package com.sap.cic.pdp.handlers;

import cds.gen.com.sap.cic.pdp.DatabricksJobs_;
import cds.gen.com.sap.cic.pdp.SchedulerRun;
import cds.gen.com.sap.cic.pdp.SchedulerRun_;
import cds.gen.orchestratorservice.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.sap.cds.Result;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnInsert;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.messages.Messages;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cds.services.request.UserInfo;
import com.sap.cic.pdp.autowired.IdempotencyTokenGenerator;
import com.sap.cic.pdp.constants.DataBricksJobStatusEnum.*;
import com.sap.cic.pdp.jsonStructures.CreateJobRequestWithNewClusterJsonStr;
import com.sap.cic.pdp.jsonStructures.DatabricksNewClusterJsonStr;
import com.sap.cic.pdp.jsonStructures.DeleteJobRequestJsonStr;
import com.sap.cic.pdp.jsonStructures.TriggerJobRequestJsonStr;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledFuture;

import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Component;

import static com.sap.cic.pdp.constants.DatabricksConstants.*;

@EnableScheduling
@Component
@ServiceName(OrchestratorService_.CDS_NAME)
public class SparkSubmitJobHandler implements EventHandler {

  @Autowired
  private PersistenceService db;

  @Autowired
  OAuth2AuthorizedClientManager authorizedClientManager;

  @Autowired
  HttpClient client;

  @Autowired
  IdempotencyTokenGenerator idempotencyTokenGenerator;

  @Autowired
  UserInfo userInfo;

  private static final Logger log = LoggerFactory.getLogger(SparkSubmitJobHandler.class);

  @Autowired
  Messages messages;

  MessageSource messageSource;

  boolean schedularFlag = false;

  public void setLatch(CountDownLatch latch) {
    this.latch = latch;
  }

  CountDownLatch latch;

  Long runId;

  @Autowired
  private ScheduledAnnotationBeanPostProcessor postProcessor;

  String jobRunningStatus;

  @Autowired
  TaskScheduler taskScheduler;
  ScheduledFuture<?> scheduledFuture;
  ObjectWriter objectWriter;
  ObjectMapper objectMapper;


  public SparkSubmitJobHandler(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  public void adjustForecast(String path) throws IOException, InterruptedException {

    //create process forecast
    try {
      createSparkJobs("AdjustForecast",
              PROCESS_FORECAST_JOB, List.of(PROCESS_FORECAST_JAR), PROCESS_FORECAST_MAIN_CLASS, path);
    } catch (IOException | InterruptedException dlStraemException) {
      log.debug(MessageFormat.format(
              messageSource.getMessage("exceptionRaised", null, null),
              PROCESS_FORECAST_JOB));
    }
    //create batching job
    try {
      createSparkJobs("AdjustForecast", BATCHING_JOB,
              List.of(BATCHING_JAR, NGDBC_JAR), BATCHING_MAIN_CLASS, null);
    } catch (IOException | InterruptedException dlStraemException) {
      log.debug(MessageFormat.format(
              messageSource.getMessage("exceptionRaised", null, null),
              BATCHING_JOB));
    }
  }
  @On(event = "masterDataDeltaLoad")
  public void masterDataDeltaLoad(MasterDataDeltaLoadContext context) throws IOException, InterruptedException {

    Long schedulerJobId = Long.valueOf(context.getParameterInfo().getHeader("x-sap-job-id"));
    Map<String, Object> properties = new HashMap<>();
    properties.put(SchedulerEntity.SCHEDULER_JOB_ID, schedulerJobId);
    CqnSelect query = Select.from(SchedulerEntity_.CDS_NAME).matching(properties);
    SchedulerEntity res = SchedulerEntity.create();
    res.setId("ac025dc9-5715-4e30-86f8-2fe2efe416ee");
    //db.run(query).listOf(SchedulerEntity.class).get(0);

    SchedulerRun entity = SchedulerRun.create();
    entity.setSchedulerIDId(res.getId());
    entity.setSchedulerRunID(context.getParameterInfo().getHeader("x-sap-job-run-id"));
    entity.setExecutionTimeStamp(Instant.now());
    entity.setRunState("Scheduled");
    entity.setRunStatus("Scheduled");

    CqnInsert insert = Insert.into(SchedulerRun_.CDS_NAME).entry(entity);
    SchedulerRun result = db.run(insert).listOf(SchedulerRun.class).get(0);

    DatabricksJobsEntity dlStreamDataBricksJob = DatabricksJobsEntity.create();
    DatabricksJobsEntity batchingDataBricksJob = DatabricksJobsEntity.create();
    dlStreamDataBricksJob.setSchedulerRunIDId(result.getId());
    batchingDataBricksJob.setSchedulerRunIDId(result.getId());

    //create dl stream job
    try {
      dlStreamDataBricksJob = createSparkJobs(context.getOrchestrationId(), DL_STREAMING_JOB,
              List.of(dlStreamingApplicationJar), mainClassNameOfSparkJarClass, null);
      entity = SchedulerRun.create();
      entity.setRunState("Triggered");
      entity.setRunStatus("Completed");
      entity.setId(result.getId());
      Update.entity(SchedulerRun_.CDS_NAME).data(entity);
    } catch (IOException | InterruptedException dlStraemException) {
      log.debug(MessageFormat.format(
              messageSource.getMessage("exceptionRaised", null, null),
              DL_STREAMING_JOB));
      entity=SchedulerRun.create();
      entity.setRunState("Request_Error");
      entity.setRunStatus("Completed");
      entity.setId(result.getId());
      Update.entity(SchedulerRun_.CDS_NAME).data(entity);
    }

    //create batching job
    try {
      batchingDataBricksJob = createSparkJobs(context.getOrchestrationId(), BATCHING_JOB,
              List.of(BATCHING_JAR, NGDBC_JAR), BATCHING_MAIN_CLASS, null);
      entity.setRunState("Triggered");
      entity.setRunStatus("Completed");
      entity.setId(result.getId());
      Update.entity(SchedulerRun_.CDS_NAME).data(entity);
    } catch (IOException | InterruptedException dlStraemException) {

      log.debug(MessageFormat.format(
              messageSource.getMessage("exceptionRaised", null, null),
              BATCHING_JOB));
      entity=SchedulerRun.create();
      entity.setRunState("Request_Error");
      entity.setRunStatus("Completed");
      entity.setId(result.getId());
      Update.entity(SchedulerRun_.CDS_NAME).data(entity);
    }

    List<DatabricksJobsEntity> databricksJobsEntityList = new ArrayList<>();
    databricksJobsEntityList.add(dlStreamDataBricksJob);
    databricksJobsEntityList.add(batchingDataBricksJob);
    context.setResult(databricksJobsEntityList);
  }

  private DatabricksJobsEntity createSparkJobs(String context, String jobExecutionName,
                                               List<String> jars, String mainClassName, String path) throws IOException, InterruptedException {

    log.debug(MessageFormat.format(
        messageSource.getMessage("submissionOfSparkJob", null, null),
        jobExecutionName));

    String localJobRunningStatus = "";
    long localRunId = 0;
    long localJobId = 0;
    String localJobName = "";
    // for converting to json structure
    objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    objectMapper = new ObjectMapper();

    // create job to submit the spark application for DL streaming
    // create random ID
    try {
      localJobName = GetUniqueId(jobExecutionName);
      localJobId = createJob(objectWriter, objectMapper, localJobName, jars, mainClassName, path);
      log.debug(MessageFormat.format(
          messageSource.getMessage("jobCreatedFor", null, null),
          jobExecutionName, localJobId, localJobName));
    } catch (IOException | InterruptedException runexception) {
      log.debug(MessageFormat.format(
          messageSource.getMessage("jobCreationFailedFor", null, null),
          jobExecutionName));
    }

    // run the job
    try {
      localRunId = runJob(objectWriter, localJobId, objectMapper);
      log.debug(MessageFormat.format(
          messageSource.getMessage("triggerJobRun", null, null),
          jobExecutionName, localRunId));
    } catch (IOException | InterruptedException runexception) {
      log.debug(MessageFormat.format(
          messageSource.getMessage("triggerJobRunFailed", null, null),
          jobExecutionName, localRunId, runexception.getMessage()));
    }

    // Get the job running status for initiate the schedular
    try {
      localJobRunningStatus = readingJobRunningStatus(localRunId, objectMapper);
    } catch (IOException | InterruptedException exception) {

      log.debug(MessageFormat.format(
          messageSource.getMessage("runStatusCheckFailed", null, null),
          jobExecutionName, localRunId));
    }
    ;

    // initialize global variables
    assignGlobalProperties(localRunId, localJobRunningStatus);

    // scheduling the status check of running job in a scheduler untill it completes all its tasks
    log.debug(MessageFormat.format(
        messageSource.getMessage("schedulerForJobRunStatusStarted", null, null),
        jobExecutionName));
    setJobRunningStatus(schedularFlag, localJobRunningStatus);
    // count down
    try {
      await();
    } catch (InterruptedException e) {
      log.debug(MessageFormat.format(
          messageSource.getMessage("failureInLatch", null, null),
          e.getMessage()));
      e.printStackTrace();
    }
    ;

    log.debug(MessageFormat.format(
        messageSource.getMessage("schedulerForJobRunStatusEnded", null, null),
        jobExecutionName, jobRunningStatus));
    System.out.println("Schedular finished its task " + jobRunningStatus);
    //Delete job
    if (1 == 2) {
      HttpResponse<String> responseDelete = deleteJob(objectWriter, localJobId);
      System.out.println(responseDelete);
    }
    // Create an entry into databricks table
    DatabricksJobsEntity databricksJobsEntity = insertDataBricksJobsEntityData(context,
        localJobName,
        localJobId, localRunId, jobRunningStatus);

    // update the orchestration table
//    updateOrchestrationEntity(context, jobRunningStatus);

    return databricksJobsEntity;
  }

  private void assignGlobalProperties(long localRunId, String localjobRunningStatus
  ) {

    this.runId = localRunId;
    this.schedularFlag = true;
    this.jobRunningStatus = localjobRunningStatus;
  }


  /**
   * @param context
   * @param jobRunningStatus
   */
  private void updateOrchestrationEntity(String context, String jobRunningStatus) {
    Update updateOrchestration = Update.entity(OrchestratorEntity_.CDS_NAME)
        .where(structuredType -> structuredType.get("orchestrationId")
            .eq(context)).data("orchestrationStatus_code", jobRunningStatus);

    Result resultUp = db.run(updateOrchestration);
  }

  /**
   * @param context
   * @param jobName
   * @param jobId
   * @param runId
   * @param jobRunningStatus
   * @return
   */
  public DatabricksJobsEntity insertDataBricksJobsEntityData(String context,
      String jobName,
      Long jobId, Long runId, String jobRunningStatus) {
    // Current Date
    Date date = new Date();

    // Insert the data into databricks job entity
    DatabricksJobsEntity databricksJobs = DatabricksJobsEntity.create();
    databricksJobs.setId(UUID.randomUUID().toString());
    databricksJobs.setCreatedAt(date.toInstant());
    databricksJobs.setModifiedAt(date.toInstant());
    databricksJobs.setCreatedBy(userInfo.getName());
    databricksJobs.setModifiedBy(userInfo.getName());
    databricksJobs.setJobId(String.valueOf(jobId));
    databricksJobs.setJobName(jobName);
    databricksJobs.setJobStatusCode(jobRunningStatus);
    databricksJobs.setJobRunID(String.valueOf(runId));
    databricksJobs.setOrchestrationIDId(context);
    CqnInsert dataBricksInsert =
        Insert.into(DatabricksJobs_.CDS_NAME).entry(databricksJobs);
    Result result = db.run(dataBricksInsert);
    return databricksJobs;
  }

  /**
   * @param runId
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public String readingJobRunningStatus(Long runId, ObjectMapper objectMapper)
      throws IOException, InterruptedException {

    var jobRunningStatusUri = URI.create(dataBricksBaseUrl +
        getDataBricksGetRunIdUrl + runId);

//    var requestJobRunningStatus = createAuthenticatedRequestBuilder(jobRunningStatusUri)
//        .method("GET", HttpRequest.BodyPublishers.noBody())
//        .build();

    HttpRequest.Builder build = createAuthenticatedRequestBuilder(jobRunningStatusUri);

    var requestJobRunningStatus = build.method("GET", HttpRequest.BodyPublishers.noBody())
        .build();

    var responseJobRunningStatus = client.send(requestJobRunningStatus,
        HttpResponse.BodyHandlers.ofString());

    String responseBody = responseJobRunningStatus.body();
    JsonNode jsonNode = objectMapper.readTree(responseBody);
    String lifeCycleState = jsonNode.path("state").path("life_cycle_state").asText();
    String lifeCycleResultState = jsonNode.path("state").path("result_state").asText();
    if (lifeCycleResultState != "") {
      lifeCycleState = lifeCycleResultState;
    }
    return lifeCycleState;
  }

  /**
   * @param objectWriter
   * @param jobId
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  private HttpResponse<String> deleteJob(ObjectWriter objectWriter, Long jobId)
      throws IOException, InterruptedException {
    var deleteJob = new DeleteJobRequestJsonStr(jobId);
    String deleteJson = objectWriter.writeValueAsString(deleteJob);
    var deleteUri = URI.create(dataBricksBaseUrl + databricksDeleteJobWorkspaceUrl);
    var deleteRequest = createAuthenticatedRequestBuilder(deleteUri)
        .method("POST", HttpRequest.BodyPublishers.ofString(deleteJson))
        .build();
    var responseDelete = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
    return responseDelete;
  }

  /**
   * @param objectWriter
   * @param jobId
   * @param objectMapper
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public Long runJob(ObjectWriter objectWriter, Long jobId,
      ObjectMapper objectMapper)
      throws IOException, InterruptedException {
    var idempotencyToken = idempotencyTokenGenerator.generateIdempotencyToken();

    var jobTrigger = new TriggerJobRequestJsonStr(jobId, idempotencyToken);
    String triggerJson = objectWriter.writeValueAsString(jobTrigger);
    var triggeruri = URI.create(dataBricksBaseUrl + databricksRunJobWorkspaceUrl);

//    var requestTrigger = createAuthenticatedRequestBuilder(triggeruri)
//        .method("POST", HttpRequest.BodyPublishers.ofString(triggerJson))
//        .build();

    HttpRequest.Builder build = createAuthenticatedRequestBuilder(triggeruri);

    var requestTrigger = build.method("POST", HttpRequest.BodyPublishers.ofString(triggerJson))
        .build();

    var response = client.send(requestTrigger, HttpResponse.BodyHandlers.ofString());

    String responseBody = response.body();
    JsonNode jsonNode = objectMapper.readTree(responseBody);
    long runId = jsonNode.get("run_id").asLong();
    return runId;

  }

  /**
   * @param objectWriter
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public Long createJob(ObjectWriter objectWriter, ObjectMapper objectMapper, String jobName,
      List<String> jars, String mainClassName, String path)
      throws IOException, InterruptedException {
    var uri = URI.create(dataBricksBaseUrl + databricksCreateJobWorkspaceUrl);

    // create new cluster
    DatabricksNewClusterJsonStr newCluster = buildNewCluster();
    // get spark jar parameters
    List<String> parameters = getParameters();
    if (path!=null && !path.isEmpty())
      parameters.add("abfss://pdpdeltalake@stpdpdeltalakepoc.dfs.core.windows.net/pdp-spark-job-service/" + path);

    // build create job templates
    String createJson = buildJobTemplate(objectWriter, jars, mainClassName, jobName, parameters);

    // create job request
//    var request = createAuthenticatedRequestBuilder(uri)
//        .method("POST", HttpRequest.BodyPublishers.ofString(createJson))
//        .build();

    HttpRequest.Builder build = createAuthenticatedRequestBuilder(uri);

    var request = build.method("POST", HttpRequest.BodyPublishers.ofString(createJson))
        .build();

    var response = client.send(request, HttpResponse.BodyHandlers.ofString());

    String responseBody = response.body();
    JsonNode jsonNode = objectMapper.readTree(responseBody);
    long jobId = jsonNode.get("job_id").asLong();
    return jobId;
  }

  /**
   * @param objectWriter
   * @param jars
   * @param mainClassName
   * @param jobName
   * @param parameters
   * @return
   * @throws JsonProcessingException
   */
  private String buildJobTemplate(ObjectWriter objectWriter, List<String> jars,
      String mainClassName,
      String jobName, List<String> parameters) throws JsonProcessingException {
    List<CreateJobRequestWithNewClusterJsonStr.Library> jarsJsonList = new ArrayList<>();
    for (String jar : jars) {
      jarsJsonList.add(new CreateJobRequestWithNewClusterJsonStr.Library(jar));
    }
    var jobRequestNewCluster = new CreateJobRequestWithNewClusterJsonStr(
        jobName, buildNewCluster(),
        jarsJsonList,
        new CreateJobRequestWithNewClusterJsonStr.SparkJarTask(mainClassName, parameters));
    // Convert to JSON using Jackson
    String createJson = objectWriter.writeValueAsString(jobRequestNewCluster);
    return createJson;
  }

  /**
   * @return
   */
  public static String GetUniqueId(String jobExecutionName) {
    Date date = new Date();
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    String timeStamp = dateFormat.format(date);
    var jobName = "Submitting Spark Based " + jobExecutionName + " Application" + "-" + timeStamp;
    return jobName;
  }

  /**
   * @return
   */
  private static ArrayList<String> getParameters() {
    String[] parametersRaw = {
        "spark.kubernetes.driver.requests.memory= 4g",
        "spark.kubernetes.driver.limit.memory= 4g",
        "spark.driver.cores:4",
        "spark.driver.memory:4g",
        "timeStamp:0101202400:00:00"
    };
    return new ArrayList<>(Arrays.asList(parametersRaw));
  }

  /**
   * @return
   */
  public DatabricksNewClusterJsonStr buildNewCluster() {

    Map<String, String> sparkConf = Map.ofEntries(
        new AbstractMap.SimpleEntry<String, String>("spark.databricks.delta.preview.enabled",
            "true"),
        new AbstractMap.SimpleEntry<String, String>("fs.azure.account.key",
            "WriSbO+Z19nuAupI+qWIQDCnaWv2UbAwdUmIdv4owH/8GbUsegemJLE6xcnpt/HPwK029Kkm1fdF+AStvXcWxw==")
    );

    return new DatabricksNewClusterJsonStr(
        "7.3.x-scala2.12",
        "Standard_DS3_v2",
        3,
        sparkConf);


  }

  /**
   * @param uri
   * @return
   */
  public HttpRequest.Builder createAuthenticatedRequestBuilder(URI uri) {
    var authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("azure-ad")
        .principal("databricks-api")
        .build();
    var authorizedClient = authorizedClientManager.authorize(authorizeRequest);
    var accessToken = Objects.requireNonNull(authorizedClient).getAccessToken();
    var token = accessToken.getTokenValue();
    return HttpRequest.newBuilder().uri(uri).header("Authentication", "Bearer " + token);
  }


  /**
   * @param schedularFlagRunningFlag
   * @param jobRunningStatus
   */
  public void setJobRunningStatus(boolean schedularFlagRunningFlag, String jobRunningStatus) {
    // For initiating the schedular flag with status is PENDING
    if (schedularFlagRunningFlag && scheduledFuture == null && jobRunningStatus.equals("PENDING")) {
      String messagePattern = messageSource.getMessage("schedulerJobStarted", null, null);
      if (messagePattern != null) {
        log.debug(MessageFormat.format(messagePattern, jobRunningStatus));
      };
//      log.debug(MessageFormat.format(
//          messageSource.getMessage("schedulerJobStarted", null, null),
//          jobRunningStatus));
      this.latch = new CountDownLatch(1);
      // Start the job
      startJob();
    } else if (!schedularFlagRunningFlag && scheduledFuture != null) {
      scheduledFuture.cancel(false);
      scheduledFuture = null;
      latch.countDown();

      log.debug(MessageFormat.format(
          messageSource.getMessage("schedulerJobStopped", null, null),
          jobRunningStatus));
    }
  }

  /**
   * Starting the job with cron expression
   */
  private void startJob() {

//    scheduledFuture = taskScheduler.schedule(this::executeJob, new CronTrigger("0 * * * * ?")); // Example cron expression: every minute
    scheduledFuture = taskScheduler.schedule(() -> {
      try {
        executeJob();

      } catch (IOException | InterruptedException schedulerException) {
        // Handle or log the exception
        log.debug(MessageFormat.format(
            messageSource.getMessage("exceptionRaisedDuring", null, null),
            schedulerException.getMessage()));
      }
    }, new CronTrigger("0/10 * * * * *"));
  }

  /**
   * @throws IOException
   * @throws InterruptedException
   */
  private void executeJob() throws IOException, InterruptedException {

    String localJobRunningStatus;
    System.out.println("Executing scheduled job...");

    // Keep on checking the status of Run job
    localJobRunningStatus = readingJobRunningStatus(runId, objectMapper);

    if (scheduledFuture.equals(null) && (localJobRunningStatus.equals("PENDING")
        || localJobRunningStatus.equals("QUEUED"))) {
      // start the schedular , so make schedular task is set to true
      schedularFlag = true;
      jobRunningStatus = localJobRunningStatus;
      System.out.println(jobRunningStatus);
      // life cycle status check
    } else if (lifeCycleStateCheck(localJobRunningStatus)) {

      jobRunningStatus = localJobRunningStatus;
      System.out.println("running" + jobRunningStatus);
      // final result status check to stop the schedular
    } else if (resultStateCheck(localJobRunningStatus)) {
      // stop the schedular
      schedularFlag = false;
      jobRunningStatus = localJobRunningStatus;
      System.out.println("completed" + jobRunningStatus);
      setJobRunningStatus(schedularFlag, jobRunningStatus);
    }
  }

  private void await() throws InterruptedException {
    latch.await(); // Wait until count reaches zero
  }

  public static boolean lifeCycleStateCheck(String status) {
    return Arrays.stream(lifeCycleState.values())
        .anyMatch(enumValue -> enumValue.name().equals(status));
  }

  public static boolean resultStateCheck(String status) {
    return Arrays.stream(resultState.values())
        .anyMatch(enumValue -> enumValue.name().equals(status));
  }

  public void setScheduledFuture(ScheduledFuture<?> scheduledFutureIn){
    scheduledFuture = scheduledFutureIn;
  }
}
