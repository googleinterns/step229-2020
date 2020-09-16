// Copyright 2020 Google LLC

/**
 * @author andreeanica
 */

package com.google.sps.data;

import java.lang.ClassCastException;


// Class used to model the Information sent back
// to the web application as a JSON object
public final class JobJSON {
  public String projectId;
  public String name;
  public String id;

  // Type can have following values:
  // - JOB_TYPE_STREAMING
  // - JOB_TYPE_UNKNOWN
  // - JOB_TYPE_BATCH
  public String type;
  public String sdk = null;
  // sdkSupportStatus can be found at 
  // https://cloud.google.com/dataflow/docs/reference/rest/v1b3/projects.jobs#sdksupportstatus
  public String sdkSupportStatus = null;
  public String sdkName;
  public String region;
  public int currentWorkers;
  public String startTime;

  // Following fields stores the number of seconds
  public Double totalVCPUTime = null; // s - divide by 3600 for hr
  public Double totalMemoryTime = null; //MB s -  divide by (3600 * 1024) for GB hr
  public Double totalDiskTimeHDD = null; //GB s - divide by 3600 for GB hr
  public Double totalDiskTimeSSD = null; //GB s - divide by 3600 for GB hr
  public Integer currentVcpuCount = null;
  public Double totalStreamingData = null; //GB - multiply by 1024 for MB
  public Boolean enableStreamingEngine = false;
  public String metricTime = null;
  public Double currentMemoryUsage = null; //MB - divide by 1024 for GB
  public Double currentPdUsage = null; // GB
  public Double currentSsdUsage = null; // GB

  public String state;
  public String stateTime;

  public Double price;

  public JobJSON(String projectId, String name, String id, String type, String sdk, String sdkSupportStatus,
                    String region, int currentWorkers, String startTime, Double totalVCPUTime, Double totalMemoryTime,
                        Double totalDiskTimeHDD, Double totalDiskTimeSSD, Integer currentVcpuCount,
                            Double totalStreamingData, Boolean enableStreamingEngine, String metricTime, 
                                String state, String stateTime, Double price, String sdkName, 
                                    Double currentMemoryUsage, Double currentPdUsage, Double currentSsdUsage) {
    this.projectId = projectId;
    this.name = name;
    this.id = id;
    this.type = type;
    this.sdk = sdk;
    this.sdkSupportStatus = sdkSupportStatus;
    this.region = region;
    this.currentWorkers = currentWorkers;
    this.startTime = startTime;
    this.totalVCPUTime = totalVCPUTime;
    this.totalMemoryTime = totalMemoryTime;
    this.totalDiskTimeHDD = totalDiskTimeHDD;
    this.totalDiskTimeSSD = totalDiskTimeSSD;
    this.currentVcpuCount = currentVcpuCount;
    this.totalStreamingData = totalStreamingData;
    this.enableStreamingEngine = enableStreamingEngine;
    this.currentMemoryUsage = currentMemoryUsage;
    this.currentPdUsage = currentPdUsage;
    this.currentSsdUsage = currentSsdUsage;

    this.metricTime = metricTime;
    this.state = state;
    this.stateTime = stateTime;

    this.price = price;
    this.sdkName = sdkName;
  }
  
  // Helper function for the equals method
  // Returns true if the 2 fields are different
  private boolean notEqualsField(Double f1, Double f2) {
    if ((f1 == null && f2 != null) ||
        (f1 != null && f2 == null) ||
        (f1 != null && f2 != null && f1.compareTo(f2) != 0)) {
      return true;
    }

    return false;
  }
  
  // Helper function for the equals method
  // Returns true if the 2 fields are different
  private boolean notEqualsField(Integer f1, Integer f2) {
    if ((f1 == null && f2 != null) ||
        (f1 != null && f2 == null) ||
        (f1 != null && f2 != null && f1.compareTo(f2) != 0)) {
      return true;
    }

    return false;
  }
  
  // Helper function for the equals method
  // Returns true if the 2 fields are different
  private boolean notEqualsField(String f1, String f2) {
    if ((f1 == null && f2 != null) ||
        (f1 != null && f2 == null) ||
        (f1 != null && f2 != null && f1.compareTo(f2) != 0)) {
      return true;
    }

    return false;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof JobJSON)) {
      return false;
    }
    JobJSON job2 = (JobJSON) obj;

    if (notEqualsField(this.id, job2.id)) {
      return false;
    }
    if (notEqualsField(this.projectId, job2.projectId)) {
      return false;
    }
    if (notEqualsField(this.name, job2.name )) {
      return false;
    }
    if (notEqualsField(this.type , job2.type)) {
      return false;
    }
    if (notEqualsField(this.sdk, job2.sdk)) {
      return false;
    }
    if (notEqualsField(this.sdkSupportStatus, job2.sdkSupportStatus)) {
      return false;
    }
    if (notEqualsField(this.region, job2.region)) {
      return false;
    }
    if (this.currentWorkers != job2.currentWorkers) {
      return false;
    }
    if (notEqualsField(this.startTime, job2.startTime)) {
      return false;
    }
    if (notEqualsField(this.totalVCPUTime, job2.totalVCPUTime)) {
      return false;
    }
    if (notEqualsField(this.totalMemoryTime, job2.totalMemoryTime)) {
      return false;
    }
    if (notEqualsField(this.totalDiskTimeHDD, job2.totalDiskTimeHDD)) {
      return false;
    }
    if (notEqualsField(this.totalDiskTimeSSD, job2.totalDiskTimeSSD)) {
      return false;
    }
    if (notEqualsField(this.currentVcpuCount, job2.currentVcpuCount)) {
      return false;
    }
    if (this.enableStreamingEngine != job2.enableStreamingEngine) {
      return false;
    }
    if (notEqualsField(this.totalStreamingData, job2.totalStreamingData)) {
      return false;
    }
    if (notEqualsField(this.currentMemoryUsage, job2.currentMemoryUsage)) {
      return false;
    }
    if (notEqualsField(this.currentPdUsage, job2.currentPdUsage)) {
      return false;
    }
    if (notEqualsField(this.currentSsdUsage, job2.currentSsdUsage)) {
      return false;
    }
    if (notEqualsField(this.metricTime, job2.metricTime)) {
      return false;
    }
    if (notEqualsField(this.state, job2.state)) {
      return false;
    }
    if (notEqualsField(this.stateTime, job2.stateTime)) {
      return false;
    }
    if (notEqualsField(this.sdkName, job2.sdkName)) {
      return false;
    }
    if (notEqualsField(this.price, job2.price)) {
      return false;
    }

    return true;
  }

  @Override
  public String toString() {
    return "JOb: " + name + " from Project: " + projectId;
  }
}