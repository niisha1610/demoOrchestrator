/*
 * ************************************************************************
 *  (C) 2024 SAP SE or an SAP affiliate company. All rights reserved. *
 * ************************************************************************
 */

package com.sap.cic.pdp.jsonStructures;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CreateJobRequestWithNewClusterJsonStr {
  @JsonProperty("name")
  public String name;
  @JsonProperty("new_cluster")
  public DatabricksNewClusterJsonStr newCluster;
  @JsonProperty("libraries")
  public List<Library> libraries;
  @JsonProperty("spark_jar_task")
  public SparkJarTask sparkJarTask;

  @Data
  @AllArgsConstructor
  public static class Library {

    public String jar;
  }

  @Data
  @AllArgsConstructor
  public static class SparkJarTask {

    @JsonProperty("main_class_name")
    public String mainClassName;
    @JsonProperty("parameters")
    public List<String> parameters;
  }
}
