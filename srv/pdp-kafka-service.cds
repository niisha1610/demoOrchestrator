service AdjustForecastService {
    event ForecastAdjustments {
        adjustmentName     : String(40);
        products           : array of productMessage;
        locations          : array of locationMessage;
        adjustmentTypeCode : String(1);
        dateFrom           : Date;
        dateTo             : Date;
        adjustmentValue    : Decimal(18, 2);
        overlappingRule    : String(4);
        };
    
    type productMessage {
        id                                    : String(40);
        internalUUID                          : String(120);
        articleHierarchyDirectoryUUID         : String(128);
        articleHierarchyDirectory             : String(2);
        articleHierarchyNodeInternalUUID      : String(128);
        articleHierarchyNode                  : String(10)

    }
    
    type locationMessage {
        id           : String(32);
        internalUUID : String(128);
    }
}