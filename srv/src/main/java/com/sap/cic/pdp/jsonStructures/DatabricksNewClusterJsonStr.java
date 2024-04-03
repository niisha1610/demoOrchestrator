/*
 * ************************************************************************
 *  (C) 2024 SAP SE or an SAP affiliate company. All rights reserved. *
 * ************************************************************************
 */

package com.sap.cic.pdp.jsonStructures;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class DatabricksNewClusterJsonStr {

    @JsonProperty("spark_version")
    private String sparkVersion;

    @JsonProperty("node_type_id")
    private String nodeTypeId;

    @JsonProperty("num_workers")
    private Integer numWorkers;
//    @JsonProperty("driver_node_type_id")
//    private String driverNodeTypeId;

    @JsonProperty("spark_conf")
    private Map<String, String> sparkConf;


//    @JsonProperty("autoscale")
//    private AutoScale autoScale;

//    @Data
//    @AllArgsConstructor
//    public static class AutoScale {
//        @JsonProperty("min_workers")
//        private Integer minWorkers;
//        @JsonProperty("max_workers")
//        private Integer maxWorkers;
//    }


}
