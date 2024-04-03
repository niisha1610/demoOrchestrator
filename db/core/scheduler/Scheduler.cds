namespace com.sap.cic.pdp;

using {com.sap.cic.pdp.Orchestration as orchestration} from '../orchestration/Orchestration';

using {
    managed,
    cuid,
} from '@sap/cds/common';

@cds.on.insert
entity Scheduler : cuid, managed {
        schedulerJobID  : Int64;
        schedulerID     : String(128);
        schedulerName   : String(20);
        schedulerDescription : String(50);
        startTime       : Timestamp;
        endTime         : Timestamp;
        reccurance      : String(50);
        orchestration   : Association to one orchestration;
 }
