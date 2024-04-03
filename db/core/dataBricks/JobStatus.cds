namespace com.sap.cic.pdp;

using {sap.common.CodeList} from '@sap/cds/common';

type jobStatusEnum : String @assert.range enum {
    PENDING;
    SKIPPED;
    SUCCEEDED;
    FAILED;
    TERMINATING;
    TERMINATED;
    INTERNAL_ERROR;
    TIMED_OUT;
    CANCELLED;
    CANCELLING;
    WAITING_FOR_RETRY;
}

@cds.persistence.skip: false
entity JobStatus : CodeList {
    key code : jobStatusEnum;
}
