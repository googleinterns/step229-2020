// Copyright 2020 Google LLC

/**
 * @author andreeanica
 */

package com.google.sps.data;

import java.io.IOException;

public class FetchJobStub implements FetchJobInfo {
  private JobJSON job;
  boolean updated;

  public void setJob(JobJSON job) {
    this.job = job;
  }

  public void setIfUpdated(boolean updated) {
    this.updated = updated;
  }

  public String getName() {
    return job.name;
  }
  
  public String getId() {
    return job.id;
  }

  public String getType() {
    return job.type;
  }

  public String getSdk() {
    return job.sdk;
  }

  public String getSdkSupportStatus() {
    return job.sdkSupportStatus;
  }

  public String getRegion() {
    return job.region;
  }

  public int getCurrentWorkers() {
    return job.currentWorkers;
  }

  public String getSdkName() {
    return job.sdkName;
  }

  public String getStartTime() {
    return job.startTime;
  }
  
  public String getState() {
    return job.state;
  }

  public String getStateTime() {
    return job.stateTime;
  }

  // Modify the job provided as parameter, setting the metrics in it
  public void setMetrics(JobModel job, String startTime) throws IOException, IllegalArgumentException {
    job.metricTime = this.job.metricTime;
    job.updated = updated;

    job.totalVCPUTime = this.job.totalVCPUTime;
    job.totalMemoryTime = this.job.totalMemoryTime;
    job.totalDiskTimeHDD = this.job.totalDiskTimeHDD;
    job.totalDiskTimeSSD = this.job.totalDiskTimeSSD;
    job.currentVcpuCount = this.job.currentVcpuCount;
    job.totalStreamingData = this.job.totalStreamingData;
    job.enableStreamingEngine = this.job.enableStreamingEngine;
    job.currentMemoryUsage = this.job.currentMemoryUsage;
    job.currentPdUsage = this.job.currentPdUsage;
    job.currentSsdUsage = this.job.currentSsdUsage;
  }
}