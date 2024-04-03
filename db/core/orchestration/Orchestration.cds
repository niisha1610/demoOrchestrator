namespace com.sap.cic.pdp;

using {com.sap.cic.pdp.OrchestrationType as Orchestrationtype} from './OrchestrationType';
using {
    managed,
    cuid,
    sap.common.CodeList
} from '@sap/cds/common';


entity Orchestration : cuid, managed {

    orchestrationId   : UUID @(Core.Computed: true);
    orchestrationType : Association to one Orchestrationtype;
}
