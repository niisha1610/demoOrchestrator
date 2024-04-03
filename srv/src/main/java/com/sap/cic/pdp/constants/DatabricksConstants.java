/*
 * ************************************************************************
 *  (C) 2024 SAP SE or an SAP affiliate company. All rights reserved. *
 * ************************************************************************
 */

package com.sap.cic.pdp.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

public class DatabricksConstants {

  public static final String dataBricksBaseUrl = "https://adb-4225868552007344.4.azuredatabricks.net/";
  public static final String databricksCreateJobWorkspaceUrl = "api/2.0/jobs/create";

  public static final String databricksRunJobWorkspaceUrl = "api/2.0/jobs/run-now";

  public static final String databricksDeleteJobWorkspaceUrl = "api/2.0/jobs/delete";

  public static final  String getDataBricksGetRunIdUrl = "api/2.0/jobs/runs/get?run_id=";

  public static final String dlStreamingApplicationJar = "abfss://pdpdeltalake@stpdpdeltalakepoc.dfs.core.windows.net/pdp-spark-job-service/jars/test/pdp-dl-stream-1.0-SNAPSHOT.jar";

  public static final String mainClassNameOfSparkJarClass = "com.sap.cic.pdp.dlstream.Application";

  public static final String BATCHING_JOB = "BATCHING_JOB";

  public static final String DL_STREAMING_JOB = "DL_STREAM_JOB";

  public static final String PROCESS_FORECAST_JOB = "PROCESS_FORECAST_JOB";

  public static final String PROCESS_FORECAST_JAR = "abfss://pdpdeltalake@stpdpdeltalakepoc.dfs.core.windows.net/pdp-spark-job-service/jars/pdp-process-forecast-1.0-SNAPSHOT.jar";

  public static final String PROCESS_FORECAST_MAIN_CLASS = "com.sap.cic.pdp.process.forecast.batch.Application";

  public static final String BATCHING_JAR = "abfss://pdpdeltalake@stpdpdeltalakepoc.dfs.core.windows.net/pdp-spark-job-service/jars/pdp-db-upsert-batch-1.0-SNAPSHOT.jar";

  public static final String BATCHING_MAIN_CLASS = "com.sap.cic.pdp.upsert.batch.Application";

  public static final String NGDBC_JAR = "abfss://pdpdeltalake@stpdpdeltalakepoc.dfs.core.windows.net/pdp-spark-job-service/jars/ngdbc-2.17.12.jar";
}
