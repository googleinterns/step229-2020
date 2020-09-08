// Copyright 2020 Google LLC

/**
 * @author andreeanica
 */

package com.google.sps.data;

import java.util.Date;
import java.sql.Timestamp;
import java.util.List;
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



// Class used to represent a Job in memory, extracting just the 
// information required by the analysis
public abstract class JobModel {
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
    public Boolean updated = false;

    // Possible states can be found at 
    // https://cloud.google.com/dataflow/docs/reference/rest/v1b3/projects.jobs#jobstate
    public String state;
    public String stateTime;

    public JobModel(String projectId, String id, String region) {
      this.projectId = projectId;
      this.id = id;

      name = null;
      type = null;
      sdkName = null;
      region = region;
      startTime = null;
      enableStreamingEngine = null;
    }

    public JobModel(String projectId, Job job, Dataflow dataflowService) throws IOException, IllegalArgumentException {
      this.projectId = projectId;
      name = job.getName();
      id = job.getId();
      type = job.getType();
      
      SdkVersion sdkVersion = job.getJobMetadata().getSdkVersion();
      if (sdkVersion != null) {
          sdk = sdkVersion.getVersion();
          sdkSupportStatus =  sdkVersion.getSdkSupportStatus();
      }

      region = job.getLocation();

      Environment environment = job.getEnvironment();
      if (environment != null) {
          currentWorkers = 0;
          List<WorkerPool> workerPoolList = environment.getWorkerPools();
          if (workerPoolList != null) {
            for (WorkerPool workerPool : workerPoolList) {
              currentWorkers += workerPool.getNumWorkers();
            }
          }

          Map<String, Object> userAgent = environment.getUserAgent();
          sdkName = (String) userAgent.get("name");
      }

      startTime = job.getStartTime();

      getMetrics(dataflowService, null);
    }

    private void getMetrics(Dataflow dataflowService, String startTime) throws IOException, IllegalArgumentException {
      Dataflow.Projects.Locations.Jobs.GetMetrics request2 = dataflowService.projects()
      .locations()
      .jobs()
      .getMetrics(projectId, region, id);
      request2.setStartTime(startTime);
      JobMetrics jobMetric = request2.execute();

      metricTime = jobMetric.getMetricTime();

      if (jobMetric != null) {
        // The job has failed, so it has no metrics
        if (jobMetric.getMetrics() == null) {
          return;
        } 
        for (MetricUpdate metric : jobMetric.getMetrics()) {
          String metricName = metric.getName().getName();
          if (metricName.compareTo("TotalVcpuTime") == 0) {
            totalVCPUTime = ((BigDecimal) metric.getScalar()).doubleValue();
            updated = true;
          } else if (metricName.compareTo("TotalMemoryUsage") == 0) {
            totalMemoryTime = ((BigDecimal) metric.getScalar()).doubleValue();
            updated = true;
          } else if (metricName.compareTo("TotalPdUsage") == 0 ) {
            totalDiskTimeHDD = ((BigDecimal) metric.getScalar()).doubleValue();
            updated = true;
          } else if (metricName.compareTo("TotalSsdUsage") == 0 ) {
            totalDiskTimeSSD = ((BigDecimal) metric.getScalar()).doubleValue();
            updated = true;
          } else if (metricName.compareTo("CurrentVcpuCount") == 0) {
            currentVcpuCount = ((BigDecimal) metric.getScalar()).intValue();
            updated = true;
          } else if (metricName.compareTo("TotalStreamingDataProcessed") == 0) {
            totalStreamingData = ((BigDecimal) metric.getScalar()).doubleValue();
            enableStreamingEngine = true;
            updated = true;
          }
        }
      }    
    }

    public static JobModel createJob(String projectId, Job job, Dataflow dataflowService)
        throws IOException, IllegalArgumentException {
      String state = job.getCurrentState();
      if (state.compareTo("JOB_STATE_UNKNOWN") == 0 
             || state.compareTo("JOB_STATE_STOPPED") == 0
             || state.compareTo("JOB_STATE_RUNNING") == 0
             || state.compareTo("JOB_STATE_DRAINING") == 0
             || state.compareTo("JOB_STATE_PENDING") == 0
             || state.compareTo("JOB_STATE_CANCELLING") == 0
             || state.compareTo("JOB_STATE_QUEUED") == 0) {
        return new RunningJob(projectId, job, dataflowService);
      } else {
        return new FinalisedJob(projectId, job, dataflowService);
      }
    }

    public static JobModel updateJob(String projectId, Job job, Dataflow dataflowService, String lastModified) 
        throws IOException, IllegalArgumentException {
      JobModel updatedJob;
      String state = job.getCurrentState();
      if (state.compareTo("JOB_STATE_UNKNOWN") == 0 
             || state.compareTo("JOB_STATE_STOPPED") == 0
             || state.compareTo("JOB_STATE_RUNNING") == 0
             || state.compareTo("JOB_STATE_DRAINING") == 0
             || state.compareTo("JOB_STATE_PENDING") == 0
             || state.compareTo("JOB_STATE_CANCELLING") == 0
             || state.compareTo("JOB_STATE_QUEUED") == 0) {
        updatedJob =  new RunningJob(projectId, job.getId(), job.getCurrentState(), job.getCurrentStateTime(), job.getLocation());
      } else {
        updatedJob = new FinalisedJob(projectId, job.getId(), job.getCurrentState(), job.getCurrentStateTime(), job.getLocation());
      }

      updatedJob.getMetrics(dataflowService, lastModified);

      return updatedJob;
    }
}
