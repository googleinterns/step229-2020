// Copyright 2020 Google LLC

/**
 * @author andreeanica
 */

package com.google.sps.data;


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

  public String state;
  public String stateTime;

  public Double price;

  public JobJSON(String projectId, String name, String id, String type, String sdk, String sdkSupportStatus,
                    String region, int currentWorkers, String startTime, Double totalVCPUTime, Double totalMemoryTime,
                        Double totalDiskTimeHDD, Double totalDiskTimeSSD, Integer currentVcpuCount,
                            Double totalStreamingData, Boolean enableStreamingEngine, String metricTime, 
                                String state, String stateTime, Double price, String sdkName) {
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
    this.metricTime = metricTime;
    this.state = state;
    this.stateTime = stateTime;

    this.price = price;
    this.sdkName = sdkName;
  }
}