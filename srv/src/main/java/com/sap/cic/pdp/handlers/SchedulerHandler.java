package com.sap.cic.pdp.handlers;

import cds.gen.orchestratorservice.OrchestratorService_;
import cds.gen.orchestratorservice.SchedulerEntity;
import cds.gen.orchestratorservice.SchedulerEntity_;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnAnalyzer;
import com.sap.cds.ql.cqn.CqnDelete;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.CqnUpdate;
import com.sap.cds.services.cds.CdsCreateEventContext;
import com.sap.cds.services.cds.CdsDeleteEventContext;
import com.sap.cds.services.cds.CdsUpdateEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cic.pdp.entity.Configuration;
import com.sap.cic.pdp.jsonStructures.JobSchedule;
import com.sap.cic.pdp.jsonStructures.Schedule;
import com.sap.cic.pdp.jsonStructures.ScheduleData;
import com.sap.cic.pdp.jsonStructures.Time;
import com.sap.cic.pdp.oauthtoken.OAuth2TokenClient;
import com.sap.cic.pdp.utils.ConfigLoadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import static com.sap.cic.pdp.constants.SchedularConstants.*;


@Component
@ServiceName(OrchestratorService_.CDS_NAME)
public class SchedulerHandler implements EventHandler {

    @Autowired
    HttpClient client;


    @Autowired
    OAuth2TokenClient oAuth2TokenClient;

    @Autowired
    PersistenceService persistenceService;

    @On(event = CqnService.EVENT_CREATE,entity = SchedulerEntity_.CDS_NAME)
    public void createSchedule(CdsCreateEventContext cdsCreateEventContext, SchedulerEntity schedulerEntity) throws IOException, InterruptedException {

        String createJobSchedule=createJobSchedule(schedulerEntity.getSchedulerName(),schedulerEntity.getSchedulerDescription(),
                schedulerEntity.getOrchestrationId(),schedulerEntity.getStartTime(),schedulerEntity.getEndTime(),schedulerEntity.getReccurance(),"POST",true);

        HttpRequest.Builder build = createAuthenticatedRequestBuilder(URI.create(SCHEDULER_URI+SCHEDULER_CREATE));


        HttpRequest request = build.method("POST", HttpRequest.BodyPublishers.ofString(createJobSchedule))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() == HTTPResponse.SC_CREATED){
            String responseBody = response.body();
            // Parse JSON string into a Map
            Map<String, Object> dataMap = parseJson(responseBody);

            // Extract _id
            int id = (Integer) dataMap.get("_id");

            // Access schedules array (assuming it's a list)
            List<Map<String, Object>> schedules = (List<Map<String, Object>>) dataMap.get("schedules");

            // Assuming there's only one schedule, get the first element
            Map<String, Object> schedule = schedules.get(0);

            // Extract scheduleId
            String scheduleId = (String) schedule.get("scheduleId");
            schedulerEntity.setSchedulerID(scheduleId);
            schedulerEntity.setSchedulerJobID(Long.valueOf(id));

        }else {
            throw new RuntimeException("Failed to create job schedule");
        }
    }

    @On(event = CqnService.EVENT_UPDATE,entity = SchedulerEntity_.CDS_NAME)
    public void updateSchedule(CdsUpdateEventContext cdsUpdateEventContext, SchedulerEntity schedulerEntity) throws IOException, InterruptedException {
        String updateJobSchedule=createJobSchedule(schedulerEntity.getSchedulerName(),schedulerEntity.getSchedulerDescription(),
                schedulerEntity.getOrchestrationId(),schedulerEntity.getStartTime(),schedulerEntity.getEndTime(),schedulerEntity.getReccurance(),"POST",false);
        Map<String, Object> rootKeys = CqnAnalyzer.create(cdsUpdateEventContext.getModel()).analyze(cdsUpdateEventContext.getCqn().ref()).rootKeys();

        CqnSelect select = Select.from(SchedulerEntity_.class).where(s -> s.ID().eq((String) rootKeys.get("ID")));

        SchedulerEntity entity=persistenceService.run(select).listOf(SchedulerEntity.class).get(0);

        String updateURI=String.format(SCHEDULER_UPDATE,entity.getSchedulerJobID(),entity.getSchedulerID());
        HttpRequest.Builder build = createAuthenticatedRequestBuilder(URI.create(SCHEDULER_URI+updateURI));
        HttpRequest request = build.method("PUT", HttpRequest.BodyPublishers.ofString(updateJobSchedule))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() == HTTPResponse.SC_OK){
            CqnUpdate update = Update.entity(SchedulerEntity_.class).data(schedulerEntity).matching(rootKeys);
            persistenceService.run(update);
        }else {
            throw new RuntimeException("Failed to create job schedule");
        }

    }

    @On(event = CqnService.EVENT_DELETE,entity = SchedulerEntity_.CDS_NAME)
    public void deleteSchedule(CdsDeleteEventContext cdsDeleteEventContext, SchedulerEntity schedulerEntity) throws IOException, InterruptedException {

        Map<String, Object> rootKeys = CqnAnalyzer.create(cdsDeleteEventContext.getModel()).analyze(cdsDeleteEventContext.getCqn().ref()).rootKeys();

        CqnSelect select = Select.from(SchedulerEntity_.class).where(s -> s.ID().eq((String) rootKeys.get("ID")));

        SchedulerEntity entity=persistenceService.run(select).listOf(SchedulerEntity.class).get(0);

        String deleteURI=String.format(SCHEDULER_DELETE,entity.getSchedulerJobID());
        HttpRequest.Builder build = createAuthenticatedRequestBuilder(URI.create(SCHEDULER_URI+deleteURI));
        HttpRequest request = build.DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() == HTTPResponse.SC_OK){
            CqnDelete delete = Delete.from(SchedulerEntity_.class).matching(rootKeys);
            persistenceService.run(delete);
        }else {
            throw new RuntimeException("Failed to create job schedule");
        }

    }

    public String createJobSchedule(String name, String description,
                                    String orchestrationId,
                                    Instant startTimeDate,
                                    Instant endTimeDate,String recurrence, String HTTPMethod, boolean isCreate) throws JsonProcessingException {

        Configuration config = ConfigLoadUtils.getConfiguration(CONFIG_YAML_FILE);
        JobSchedule request = new JobSchedule(); // No constructor call

        // Set mandatory fields (assuming name and cron are mandatory)
        if(name!=null)
            request.setName(name);

        // Set other optional fields using setters (consider validation)
        if(description!=null)
            request.setDescription(description);
        if(startTimeDate!=null)
            request.setStartTime(new Time(startTimeDate));
        if(endTimeDate!=null)
            request.setEndTime(new Time(endTimeDate));
        if(isCreate){
            request.setHttpMethod(HTTPMethod);
            request.setAction(config.getMasterDataDeltaLoadURL());
            request.setActive(true);
        }
        // Create Scheduleobjects and populate data
        List<Schedule> schedules = new ArrayList<>();
        schedules.add(createSchedule(orchestrationId, startTimeDate, endTimeDate,recurrence,description));
        request.setSchedules(schedules);

        // Use ObjectMapper for JSON conversion
        ObjectMapper mapper = new ObjectMapper();
       // mapper.writeValueAsString(request);
        return request.toString();
    }

    private Schedule createSchedule(String orchestrationId,
                                    Instant startTimeDate, Instant endTimeDate, String recurrence,String description) {
        Schedule schedule = new Schedule();
        if(description!=null)
            schedule.setDescription(description);
        if(orchestrationId!=null)
            schedule.setData(new ScheduleData(orchestrationId));
        schedule.setActive(true);
        if(startTimeDate!=null)
            schedule.setStartTime(new Time(startTimeDate));
        if(endTimeDate!=null)
            schedule.setEndTime(new Time(endTimeDate));
        if(recurrence!=null)
            schedule.setTime(recurrence);
        return schedule;
    }

    private static Map<String, Object> parseJson(String jsonString) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonString, Map.class);
    }


    private HttpRequest.Builder createAuthenticatedRequestBuilder(URI uri) throws IOException {
        return HttpRequest.newBuilder().uri(uri).header("Authorization", "Bearer " + oAuth2TokenClient.getAccessToken(clientId,clientSecret,authorizationServerUrl,grantType)).header("Content-Type","application/json");
    }
}