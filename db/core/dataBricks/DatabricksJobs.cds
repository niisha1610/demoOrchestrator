namespace com.sap.cic.pdp;

using {com.sap.cic.pdp.Orchestration as orchestration} from '../orchestration/Orchestration';
using {com.sap.cic.pdp.JobStatus as jobStatus} from './JobStatus';
using {com.sap.cic.pdp.SchedulerRun as schedulerRun} from '../scheduler/SchedulerRun';


using {
    managed,
    cuid,
} from '@sap/cds/common';

@cds.on.insert
entity DatabricksJobs : cuid, managed {

    key jobId           : String(128);
        jobName         : String(255);
        jobStatus       : Association to one jobStatus;
        jobRunID        : String(128);
        orchestrationID : Association to one orchestration;
        schedulerRunID  : Association to one schedulerRun;
}
