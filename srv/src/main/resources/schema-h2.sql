
DROP VIEW IF EXISTS localized_OrchestratorService_V_SchedulerRunEntity;
DROP VIEW IF EXISTS localized_OrchestratorService_SchedulerEntity;
DROP VIEW IF EXISTS localized_OrchestratorService_DatabricksJobsEntity;
DROP VIEW IF EXISTS localized_OrchestratorService_OrchestratorEntity;
DROP VIEW IF EXISTS localized_OrchestratorService_JobStatus;
DROP VIEW IF EXISTS localized_OrchestratorService_OrchestrationType;
DROP VIEW IF EXISTS localized_com_sap_cic_pdp_V_SchedulerRun;
DROP VIEW IF EXISTS OrchestratorService_V_SchedulerRunEntity;
DROP VIEW IF EXISTS localized_com_sap_cic_pdp_SchedulerRun;
DROP VIEW IF EXISTS localized_com_sap_cic_pdp_Scheduler;
DROP VIEW IF EXISTS localized_com_sap_cic_pdp_DatabricksJobs;
DROP VIEW IF EXISTS localized_com_sap_cic_pdp_Orchestration;
DROP VIEW IF EXISTS localized_com_sap_cic_pdp_JobStatus;
DROP VIEW IF EXISTS localized_com_sap_cic_pdp_OrchestrationType;
DROP VIEW IF EXISTS OrchestratorService_JobStatus_texts;
DROP VIEW IF EXISTS OrchestratorService_OrchestrationType_texts;
DROP VIEW IF EXISTS OrchestratorService_JobStatus;
DROP VIEW IF EXISTS OrchestratorService_OrchestrationType;
DROP VIEW IF EXISTS com_sap_cic_pdp_V_SchedulerRun;
DROP VIEW IF EXISTS OrchestratorService_SchedulerEntity;
DROP VIEW IF EXISTS OrchestratorService_DatabricksJobsEntity;
DROP VIEW IF EXISTS OrchestratorService_OrchestratorEntity;
DROP TABLE IF EXISTS com_sap_cic_pdp_JobStatus_texts;
DROP TABLE IF EXISTS com_sap_cic_pdp_OrchestrationType_texts;
DROP TABLE IF EXISTS com_sap_cic_pdp_JobStatus;
DROP TABLE IF EXISTS com_sap_cic_pdp_OrchestrationType;
DROP TABLE IF EXISTS com_sap_cic_pdp_SchedulerRun;
DROP TABLE IF EXISTS com_sap_cic_pdp_Scheduler;
DROP TABLE IF EXISTS com_sap_cic_pdp_DatabricksJobs;
DROP TABLE IF EXISTS com_sap_cic_pdp_Orchestration;

CREATE TABLE com_sap_cic_pdp_Orchestration (
  ID NVARCHAR(36) NOT NULL,
  createdAt TIMESTAMP(7),
  createdBy NVARCHAR(255),
  modifiedAt TIMESTAMP(7),
  modifiedBy NVARCHAR(255),
  orchestrationId NVARCHAR(36),
  orchestrationType_code INTEGER,
  PRIMARY KEY(ID)
); 

CREATE TABLE com_sap_cic_pdp_DatabricksJobs (
  ID NVARCHAR(36) NOT NULL,
  createdAt TIMESTAMP(7),
  createdBy NVARCHAR(255),
  modifiedAt TIMESTAMP(7),
  modifiedBy NVARCHAR(255),
  jobId NVARCHAR(128) NOT NULL,
  jobName NVARCHAR(255),
  jobStatus_code NVARCHAR(255),
  jobRunID NVARCHAR(128),
  orchestrationID_ID NVARCHAR(36),
  schedulerRunID_ID NVARCHAR(36),
  PRIMARY KEY(ID, jobId)
); 

CREATE TABLE com_sap_cic_pdp_Scheduler (
  ID NVARCHAR(36) NOT NULL,
  createdAt TIMESTAMP(7),
  createdBy NVARCHAR(255),
  modifiedAt TIMESTAMP(7),
  modifiedBy NVARCHAR(255),
  schedulerJobID BIGINT,
  schedulerID NVARCHAR(128),
  schedulerName NVARCHAR(20),
  schedulerDescription NVARCHAR(50),
  startTime TIMESTAMP(7),
  endTime TIMESTAMP(7),
  reccurance NVARCHAR(50),
  orchestration_ID NVARCHAR(36),
  PRIMARY KEY(ID)
); 

CREATE TABLE com_sap_cic_pdp_SchedulerRun (
  ID NVARCHAR(36) NOT NULL,
  createdAt TIMESTAMP(7),
  createdBy NVARCHAR(255),
  modifiedAt TIMESTAMP(7),
  modifiedBy NVARCHAR(255),
  schedulerRunID NVARCHAR(128),
  schedulerID_ID NVARCHAR(36),
  executionTimeStamp TIMESTAMP(7),
  nextRunTimeStamp TIMESTAMP(7),
  runStatus NVARCHAR(10),
  runState NVARCHAR(10),
  statusMessage NVARCHAR(50),
  PRIMARY KEY(ID)
); 

CREATE TABLE com_sap_cic_pdp_OrchestrationType (
  name NVARCHAR(255),
  descr NVARCHAR(1000),
  code INTEGER NOT NULL,
  criticality INTEGER,
  PRIMARY KEY(code)
); 

CREATE TABLE com_sap_cic_pdp_JobStatus (
  name NVARCHAR(255),
  descr NVARCHAR(1000),
  code NVARCHAR(255) NOT NULL,
  PRIMARY KEY(code)
); 

CREATE TABLE com_sap_cic_pdp_OrchestrationType_texts (
  locale NVARCHAR(14) NOT NULL,
  name NVARCHAR(255),
  descr NVARCHAR(1000),
  code INTEGER NOT NULL,
  PRIMARY KEY(locale, code)
); 

CREATE TABLE com_sap_cic_pdp_JobStatus_texts (
  locale NVARCHAR(14) NOT NULL,
  name NVARCHAR(255),
  descr NVARCHAR(1000),
  code NVARCHAR(255) NOT NULL,
  PRIMARY KEY(locale, code)
); 

CREATE VIEW OrchestratorService_OrchestratorEntity AS SELECT
  Orchestration_0.ID,
  Orchestration_0.createdAt,
  Orchestration_0.createdBy,
  Orchestration_0.modifiedAt,
  Orchestration_0.modifiedBy,
  Orchestration_0.orchestrationId,
  Orchestration_0.orchestrationType_code
FROM com_sap_cic_pdp_Orchestration AS Orchestration_0; 

CREATE VIEW OrchestratorService_DatabricksJobsEntity AS SELECT
  DatabricksJobs_0.ID,
  DatabricksJobs_0.createdAt,
  DatabricksJobs_0.createdBy,
  DatabricksJobs_0.modifiedAt,
  DatabricksJobs_0.modifiedBy,
  DatabricksJobs_0.jobId,
  DatabricksJobs_0.jobName,
  DatabricksJobs_0.jobStatus_code,
  DatabricksJobs_0.jobRunID,
  DatabricksJobs_0.orchestrationID_ID,
  DatabricksJobs_0.schedulerRunID_ID
FROM com_sap_cic_pdp_DatabricksJobs AS DatabricksJobs_0; 

CREATE VIEW OrchestratorService_SchedulerEntity AS SELECT
  Scheduler_0.ID,
  Scheduler_0.createdAt,
  Scheduler_0.createdBy,
  Scheduler_0.modifiedAt,
  Scheduler_0.modifiedBy,
  Scheduler_0.schedulerJobID,
  Scheduler_0.schedulerID,
  Scheduler_0.schedulerName,
  Scheduler_0.schedulerDescription,
  Scheduler_0.startTime,
  Scheduler_0.endTime,
  Scheduler_0.reccurance,
  Scheduler_0.orchestration_ID
FROM com_sap_cic_pdp_Scheduler AS Scheduler_0; 

CREATE VIEW com_sap_cic_pdp_V_SchedulerRun AS SELECT
  sr_0.schedulerRunID,
  sr_0.executionTimeStamp,
  dbj_1.jobId,
  dbj_1.jobName,
  orchestrationType_3.name,
  sr_0.runStatus
FROM (((com_sap_cic_pdp_SchedulerRun AS sr_0 INNER JOIN com_sap_cic_pdp_DatabricksJobs AS dbj_1 ON sr_0.ID = dbj_1.schedulerRunID_ID) INNER JOIN com_sap_cic_pdp_Orchestration AS o_2 ON dbj_1.orchestrationID_ID = o_2.ID) LEFT JOIN com_sap_cic_pdp_OrchestrationType AS orchestrationType_3 ON o_2.orchestrationType_code = orchestrationType_3.code); 

CREATE VIEW OrchestratorService_OrchestrationType AS SELECT
  OrchestrationType_0.name,
  OrchestrationType_0.descr,
  OrchestrationType_0.code,
  OrchestrationType_0.criticality
FROM com_sap_cic_pdp_OrchestrationType AS OrchestrationType_0; 

CREATE VIEW OrchestratorService_JobStatus AS SELECT
  JobStatus_0.name,
  JobStatus_0.descr,
  JobStatus_0.code
FROM com_sap_cic_pdp_JobStatus AS JobStatus_0; 

CREATE VIEW OrchestratorService_OrchestrationType_texts AS SELECT
  texts_0.locale,
  texts_0.name,
  texts_0.descr,
  texts_0.code
FROM com_sap_cic_pdp_OrchestrationType_texts AS texts_0; 

CREATE VIEW OrchestratorService_JobStatus_texts AS SELECT
  texts_0.locale,
  texts_0.name,
  texts_0.descr,
  texts_0.code
FROM com_sap_cic_pdp_JobStatus_texts AS texts_0; 

CREATE VIEW localized_com_sap_cic_pdp_OrchestrationType AS SELECT
  coalesce(localized_1.name, L_0.name) AS name,
  coalesce(localized_1.descr, L_0.descr) AS descr,
  L_0.code,
  L_0.criticality
FROM (com_sap_cic_pdp_OrchestrationType AS L_0 LEFT JOIN com_sap_cic_pdp_OrchestrationType_texts AS localized_1 ON localized_1.code = L_0.code AND localized_1.locale = @locale); 

CREATE VIEW localized_com_sap_cic_pdp_JobStatus AS SELECT
  coalesce(localized_1.name, L_0.name) AS name,
  coalesce(localized_1.descr, L_0.descr) AS descr,
  L_0.code
FROM (com_sap_cic_pdp_JobStatus AS L_0 LEFT JOIN com_sap_cic_pdp_JobStatus_texts AS localized_1 ON localized_1.code = L_0.code AND localized_1.locale = @locale); 

CREATE VIEW localized_com_sap_cic_pdp_Orchestration AS SELECT
  L.ID,
  L.createdAt,
  L.createdBy,
  L.modifiedAt,
  L.modifiedBy,
  L.orchestrationId,
  L.orchestrationType_code
FROM com_sap_cic_pdp_Orchestration AS L; 

CREATE VIEW localized_com_sap_cic_pdp_DatabricksJobs AS SELECT
  L.ID,
  L.createdAt,
  L.createdBy,
  L.modifiedAt,
  L.modifiedBy,
  L.jobId,
  L.jobName,
  L.jobStatus_code,
  L.jobRunID,
  L.orchestrationID_ID,
  L.schedulerRunID_ID
FROM com_sap_cic_pdp_DatabricksJobs AS L; 

CREATE VIEW localized_com_sap_cic_pdp_Scheduler AS SELECT
  L.ID,
  L.createdAt,
  L.createdBy,
  L.modifiedAt,
  L.modifiedBy,
  L.schedulerJobID,
  L.schedulerID,
  L.schedulerName,
  L.schedulerDescription,
  L.startTime,
  L.endTime,
  L.reccurance,
  L.orchestration_ID
FROM com_sap_cic_pdp_Scheduler AS L; 

CREATE VIEW localized_com_sap_cic_pdp_SchedulerRun AS SELECT
  L.ID,
  L.createdAt,
  L.createdBy,
  L.modifiedAt,
  L.modifiedBy,
  L.schedulerRunID,
  L.schedulerID_ID,
  L.executionTimeStamp,
  L.nextRunTimeStamp,
  L.runStatus,
  L.runState,
  L.statusMessage
FROM com_sap_cic_pdp_SchedulerRun AS L; 

CREATE VIEW OrchestratorService_V_SchedulerRunEntity AS SELECT
  V_SchedulerRun_0.schedulerRunID,
  V_SchedulerRun_0.executionTimeStamp,
  V_SchedulerRun_0.jobId,
  V_SchedulerRun_0.jobName,
  V_SchedulerRun_0.name,
  V_SchedulerRun_0.runStatus
FROM com_sap_cic_pdp_V_SchedulerRun AS V_SchedulerRun_0; 

CREATE VIEW localized_com_sap_cic_pdp_V_SchedulerRun AS SELECT
  sr_0.schedulerRunID,
  sr_0.executionTimeStamp,
  dbj_1.jobId,
  dbj_1.jobName,
  orchestrationType_3.name,
  sr_0.runStatus
FROM (((localized_com_sap_cic_pdp_SchedulerRun AS sr_0 INNER JOIN localized_com_sap_cic_pdp_DatabricksJobs AS dbj_1 ON sr_0.ID = dbj_1.schedulerRunID_ID) INNER JOIN localized_com_sap_cic_pdp_Orchestration AS o_2 ON dbj_1.orchestrationID_ID = o_2.ID) LEFT JOIN localized_com_sap_cic_pdp_OrchestrationType AS orchestrationType_3 ON o_2.orchestrationType_code = orchestrationType_3.code); 

CREATE VIEW localized_OrchestratorService_OrchestrationType AS SELECT
  OrchestrationType_0.name,
  OrchestrationType_0.descr,
  OrchestrationType_0.code,
  OrchestrationType_0.criticality
FROM localized_com_sap_cic_pdp_OrchestrationType AS OrchestrationType_0; 

CREATE VIEW localized_OrchestratorService_JobStatus AS SELECT
  JobStatus_0.name,
  JobStatus_0.descr,
  JobStatus_0.code
FROM localized_com_sap_cic_pdp_JobStatus AS JobStatus_0; 

CREATE VIEW localized_OrchestratorService_OrchestratorEntity AS SELECT
  Orchestration_0.ID,
  Orchestration_0.createdAt,
  Orchestration_0.createdBy,
  Orchestration_0.modifiedAt,
  Orchestration_0.modifiedBy,
  Orchestration_0.orchestrationId,
  Orchestration_0.orchestrationType_code
FROM localized_com_sap_cic_pdp_Orchestration AS Orchestration_0; 

CREATE VIEW localized_OrchestratorService_DatabricksJobsEntity AS SELECT
  DatabricksJobs_0.ID,
  DatabricksJobs_0.createdAt,
  DatabricksJobs_0.createdBy,
  DatabricksJobs_0.modifiedAt,
  DatabricksJobs_0.modifiedBy,
  DatabricksJobs_0.jobId,
  DatabricksJobs_0.jobName,
  DatabricksJobs_0.jobStatus_code,
  DatabricksJobs_0.jobRunID,
  DatabricksJobs_0.orchestrationID_ID,
  DatabricksJobs_0.schedulerRunID_ID
FROM localized_com_sap_cic_pdp_DatabricksJobs AS DatabricksJobs_0; 

CREATE VIEW localized_OrchestratorService_SchedulerEntity AS SELECT
  Scheduler_0.ID,
  Scheduler_0.createdAt,
  Scheduler_0.createdBy,
  Scheduler_0.modifiedAt,
  Scheduler_0.modifiedBy,
  Scheduler_0.schedulerJobID,
  Scheduler_0.schedulerID,
  Scheduler_0.schedulerName,
  Scheduler_0.schedulerDescription,
  Scheduler_0.startTime,
  Scheduler_0.endTime,
  Scheduler_0.reccurance,
  Scheduler_0.orchestration_ID
FROM localized_com_sap_cic_pdp_Scheduler AS Scheduler_0; 

CREATE VIEW localized_OrchestratorService_V_SchedulerRunEntity AS SELECT
  V_SchedulerRun_0.schedulerRunID,
  V_SchedulerRun_0.executionTimeStamp,
  V_SchedulerRun_0.jobId,
  V_SchedulerRun_0.jobName,
  V_SchedulerRun_0.name,
  V_SchedulerRun_0.runStatus
FROM localized_com_sap_cic_pdp_V_SchedulerRun AS V_SchedulerRun_0; 

