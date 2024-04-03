/*
 * ************************************************************************
 *  (C) 2024 SAP SE or an SAP affiliate company. All rights reserved. *
 * ************************************************************************
 */

package com.sap.cic.pdp.constants;

public class DataBricksJobStatusEnum {

  public enum resultState {
    FAILED,
    SUCCESS,
    TIMEDOUT,
    CANCELED,
    MAXIMUM_CONCURRENT_RUNS_REACHED,
    EXCLUDED,
    SUCCESS_WITH_FAILURES,
    UPSTREAM_FAILED,
    UPSTREAM_CANCELED
  }

  public enum lifeCycleState {
    PENDING,
    RUNNING,
    TERMINATING,
    TERMINATED,
    SKIPPED,
    INTERNAL_ERROR,
    BLOCKED,
    WAITING_FOR_RETRY,
    QUEUED
  }

}
