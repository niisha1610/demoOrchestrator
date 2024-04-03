namespace com.sap.cic.pdp;

using { com.sap.cic.pdp.SchedulerRun } from '../scheduler/SchedulerRun';
using { com.sap.cic.pdp.DatabricksJobs } from '../dataBricks/DatabricksJobs';
using { com.sap.cic.pdp.Orchestration } from '../orchestration/Orchestration';

entity V_SchedulerRun as select from SchedulerRun as sr inner join 
    DatabricksJobs as dbj on sr.ID = dbj.schedulerRunID.ID inner join 
    Orchestration as o on dbj.orchestrationID.ID = o.ID 
    {
        sr.schedulerRunID,
        sr.executionTimeStamp,
        dbj.jobId,
        dbj.jobName,
        o.orchestrationType.name,
        sr.runStatus
}  