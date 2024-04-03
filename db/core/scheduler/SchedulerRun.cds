namespace com.sap.cic.pdp;

using {com.sap.cic.pdp.Scheduler as Scheduler} from './Scheduler';


using {
    managed,
    cuid,
} from '@sap/cds/common';

entity SchedulerRun : cuid, managed {
    schedulerRunID     : String(128);
    schedulerID        : Association to one Scheduler;
    executionTimeStamp : Timestamp;
    nextRunTimeStamp   : Timestamp;
    runStatus          : String(10);
    runState           : String(10);
    statusMessage      : String(50);
};
