namespace com.sap.cic.pdp;

using {sap.common.CodeList} from '@sap/cds/common';


type OrchestrationTypeEnum : Integer @assert.range enum {
    InitialLoad             = 1;
    AdjustForecast          = 2;
    ModellingAndForecastRun = 3;
    MasterDataDeltaLoad     = 4;

}

@cds.persistence.skip: false
entity OrchestrationType : CodeList {
    key code        : OrchestrationTypeEnum;
        criticality : Integer; //  2: yellow colour,  3: green colour, 0: unknown
};
