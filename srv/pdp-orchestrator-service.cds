using {com.sap.cic.pdp.Orchestration as Orchestration} from '../db/core/orchestration/Orchestration';
using {com.sap.cic.pdp.DatabricksJobs as DatabricksJobs} from '../db/core/dataBricks/DatabricksJobs';
using {com.sap.cic.pdp.Scheduler as Scheduler} from '../db/core/scheduler/Scheduler';
using {com.sap.cic.pdp.SchedulerRun as SchedulerRun} from '../db/core/scheduler/SchedulerRun';
using {com.sap.cic.pdp.V_SchedulerRun as V_SchedulerRun} from '../db/core/views/V_SchedulerRun';


service OrchestratorService {

    entity OrchestratorEntity   as projection on Orchestration;

    entity DatabricksJobsEntity as projection on DatabricksJobs;

    entity SchedulerEntity      as projection on Scheduler;

    entity V_SchedulerRunEntity as projection on V_SchedulerRun;

    action masterDataDeltaLoad(orchestrationId : UUID) returns array of DatabricksJobsEntity;

}

// annotate OrchestratorService.OrchestratorEntity {
//     ID                @UI.Hidden;
//     createdAt         @UI.Hidden;
//     modifiedAt        @UI.Hidden;
//     createdBy         @UI.Hidden;
//     modifiedBy        @UI.Hidden;
//     orchestrationId   @UI.Hidden;
//     description       @title: 'Description';
//     orchestrationType @title: 'Orechestration Type';

// }
