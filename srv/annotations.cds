using OrchestratorService as service from './pdp-orchestrator-service';

annotate OrchestratorService.SchedulerEntity with @(UI: {
    SelectionFields                        : [

],
    LineItem                     : [
        {
            Value                    : schedulerID,
            ![@HTML5.CssDefaults]    : {width: 'auto'},
        },
        {
            Value                : schedulerName,
            ![@HTML5.CssDefaults]: {width: 'auto'}
        },
        {
            Value                : reccurance,
            ![@HTML5.CssDefaults]: {width: 'auto'}
        },
        {
            Value                : Status,
            ![@HTML5.CssDefaults]: {width: 'auto'},
            Criticality         : 03,
            CriticalityRepresentation   : #WithIcon,
        }
    ]
});

annotate service.SchedulerEntity {
    schedulerJobID;
    schedulerID  @title: 'Scheduler ID';
    schedulerName  @title: 'Scheduler Name';
    reccurance  @title: 'Reccurance';
    orchestrationID  @title: 'OrchestrationID';
    
};
