package com.sap.cic.pdp.constants;

public class SchedularConstants {
    public static final String SCHEDULER_URI="https://jobscheduler-rest.cfapps.eu20.hana.ondemand.com";
    public static final String SCHEDULER_CREATE="/scheduler/jobs";
    public static final String SCHEDULER_UPDATE="/scheduler/jobs/%s/schedules/%s";
    public static final String SCHEDULER_DELETE="/scheduler/jobs/%s";
    public static final String clientId = "sb-f5fa515e-b2ea-40ff-9d4c-465ca3cdf77c!b27692|sap-jobscheduler!b4";
    public static final String clientSecret = "cdeb53c2-0aef-48e3-9f83-e351f30e84b9$6m1tBs44PDBiZ5ZsSwlGOaHe409miQk_jFZf_GjaeE4=";
    public static final String authorizationServerUrl = "https://pdp-dev-eu-dev.authentication.eu20.hana.ondemand.com/oauth/token";
    public static final String grantType = "client_credentials";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm Z";
    public static final String CONFIG_YAML_FILE = "/config.yaml";
}
