namespace com.sap.cic.pdp;

using {sap.common.CodeList} from '@sap/cds/common';


type OrchestrationStatusEnum : String @assert.range enum {
    Active   = 'A';
    InActive = 'I';
    Canceled = 'X';
};

@cds.persistence.skip: false
entity OrchestrationStatus : CodeList {

    key code        : OrchestrationStatusEnum;
        criticality : Integer; //  2: yellow colour,  3: green colour, 0: unknown
};
