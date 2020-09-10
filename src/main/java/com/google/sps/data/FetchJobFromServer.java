// Copyright 2020 Google LLC

/**
 * @author andreeanica
 */

package com.google.sps.data;

import com.google.api.services.dataflow.Dataflow;
import com.google.api.services.dataflow.model.Job;
import com.google.api.services.dataflow.model.JobMetadata;
import com.google.api.services.dataflow.model.SdkVersion;
import com.google.api.services.dataflow.model.Environment;
import com.google.api.services.dataflow.model.WorkerPool;
import com.google.api.services.dataflow.model.JobMetrics;
import com.google.api.services.dataflow.model.MetricUpdate;
import java.io.IOException;
import java.lang.IllegalArgumentException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.List;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;

// Class FetchJobFromServer fetches the necessary information from the 
// server. Class used in the acgtual implementation of the web application
public class FetchJobFromServer implements FetchJobInfo {
  private Job job;
  private Environment environment;
  private SdkVersion sdkVersion;
  private Dataflow dataflowService;

  public FetchJobFromServer(Job job, Dataflow dataflowService) {
    this.job = job;
    environment = job.getEnvironment();
    sdkVersion = job.getJobMetadata().getSdkVersion(); 
    this.dataflowService = dataflowService;
  }

  public String getName() {
    return job.getName();
  }

  public String getId() {
    return job.getId();
  }

  public String getType() {
    return job.getType();
  }

  public String getSdk() {
    if (sdkVersion != null) {
      return sdkVersion.getVersion();
    } else {
      return null;
    }
  }

  public String getSdkSupportStatus() {
    if (sdkVersion != null) {
      return sdkVersion.getSdkSupportStatus();
    } else {
      return null;
    }  
  }

  public String getRegion() {
    return job.getLocation();
  }

  public int getCurrentWorkers() {
    int currentWorkers = 0;
    if (environment != null) {
      List<WorkerPool> workerPoolList = environment.getWorkerPools();
      if (workerPoolList != null) {
        for (WorkerPool workerPool : workerPoolList) {
          currentWorkers += workerPool.getNumWorkers();
        }
      }
    }

    return currentWorkers;
  }

  public String getSdkName() {
    if (environment != null) {
      Map<String, Object> userAgent = environment.getUserAgent();
      return (String) userAgent.get("name");
    } else {
      return null;
    }
  }

  public String getStartTime() {
    return job.getStartTime();
  }

  public String getState() {
    return job.getCurrentState();
  }

  public String getStateTime() {
    return job.getCurrentStateTime();
  }

  public void setMetrics(JobModel jobModel, String startTime) throws IOException, IllegalArgumentException {
    Dataflow.Projects.Locations.Jobs.GetMetrics request2 = dataflowService.projects()
      .locations()
      .jobs()
      .getMetrics(jobModel.projectId, jobModel.region, jobModel.id);
      request2.setStartTime(startTime);

    JobMetrics jobMetric;

    try {
      jobMetric = request2.execute();
    } catch (GoogleJsonResponseException e) {
      // The job is no longer saved in the system, so we can't fetch any metric
      return;
    }

    jobModel.metricTime = jobMetric.getMetricTime();

    if (jobMetric != null) {
      // The job has failed, so it has no metrics
      if (jobMetric.getMetrics() == null) {
        return;
      }

      for (MetricUpdate metric : jobMetric.getMetrics()) {
        String metricName = metric.getName().getName();
        if (metricName.compareTo("TotalVcpuTime") == 0) {
          jobModel.totalVCPUTime = ((BigDecimal) metric.getScalar()).doubleValue();
          jobModel.updated = true;
        } else if (metricName.compareTo("TotalMemoryUsage") == 0) {
          jobModel.totalMemoryTime = ((BigDecimal) metric.getScalar()).doubleValue();
          jobModel.updated = true;
        } else if (metricName.compareTo("TotalPdUsage") == 0 ) {
          jobModel.totalDiskTimeHDD = ((BigDecimal) metric.getScalar()).doubleValue();
          jobModel.updated = true;
        } else if (metricName.compareTo("TotalSsdUsage") == 0 ) {
          jobModel.totalDiskTimeSSD = ((BigDecimal) metric.getScalar()).doubleValue();
          jobModel.updated = true;
        } else if (metricName.compareTo("CurrentVcpuCount") == 0) {
          jobModel.currentVcpuCount = ((BigDecimal) metric.getScalar()).intValue();
          jobModel.updated = true;
        } else if (metricName.compareTo("TotalStreamingDataProcessed") == 0) {
          jobModel.totalStreamingData = ((BigDecimal) metric.getScalar()).doubleValue();
          jobModel.enableStreamingEngine = true;
          jobModel.updated = true;
        } else if (metricName.compareTo("CurrentMemoryUsage") == 0) {
          jobModel.currentMemoryUsage = ((BigDecimal) metric.getScalar()).doubleValue();
          jobModel.updated = true;
        } else if (metricName.compareTo("CurrentPdUsage") == 0) {
          jobModel.currentPdUsage = ((BigDecimal) metric.getScalar()).doubleValue();
          jobModel.updated = true;
          } else if (metricName.compareTo("CurrentSsdUsage") == 0) {
          jobModel.currentSsdUsage = ((BigDecimal) metric.getScalar()).doubleValue();
          jobModel.updated = true;
        }
      }
    }
  }
}