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
import com.google.api.client.googleapis.json.GoogleJsonResponseException;

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
    public Double currentMemoryUsage = null; //MB - divide by 1024 for GB
    public Double currentPdUsage = null; // GB
    public Double currentSsdUsage = null; // GB
    public Boolean updated = false;

    // Possible states can be found at 
    // https://cloud.google.com/dataflow/docs/reference/rest/v1b3/projects.jobs#jobstate
    public String state;
    public String stateTime;

    public JobModel(String projectId, String id, String region, String type) {
      this.projectId = projectId;
      this.id = id;
      this.region = region;
      this.type = type;

      name = null;
      sdkName = null;
      startTime = null;
      enableStreamingEngine = null;
    }

    public JobModel(String projectId, FetchJobInfo fetchJobInfo) throws IOException, IllegalArgumentException {
      this.projectId = projectId;
      name = fetchJobInfo.getName();
      id = fetchJobInfo.getId();
      type = fetchJobInfo.getType();
      sdk = fetchJobInfo.getSdk();
      sdkSupportStatus =  fetchJobInfo.getSdkSupportStatus();
      region = fetchJobInfo.getRegion();
      currentWorkers = fetchJobInfo.getCurrentWorkers();
      sdkName = fetchJobInfo.getSdkName();
      startTime = fetchJobInfo.getStartTime();

      fetchJobInfo.setMetrics(this, null);
    }

    public static JobModel createJob(String projectId, FetchJobInfo fetchJobInfo)
        throws IOException, IllegalArgumentException {
      String state = fetchJobInfo.getState();
      if (state.compareTo("JOB_STATE_UNKNOWN") == 0 
             || state.compareTo("JOB_STATE_STOPPED") == 0
             || state.compareTo("JOB_STATE_RUNNING") == 0
             || state.compareTo("JOB_STATE_DRAINING") == 0
             || state.compareTo("JOB_STATE_PENDING") == 0
             || state.compareTo("JOB_STATE_CANCELLING") == 0
             || state.compareTo("JOB_STATE_QUEUED") == 0) {
        return new RunningJob(projectId, fetchJobInfo);
      } else {
        return new FinalisedJob(projectId, fetchJobInfo);
      }
    }
    
    // Jobs only have the metrics that were modified sincer lastUpdate different from null
    public static JobModel updateJob(String projectId, FetchJobInfo fetchJobInfo, String lastModified) 
        throws IOException, IllegalArgumentException {
      JobModel updatedJob;
      String state = fetchJobInfo.getState();
      if (state.compareTo("JOB_STATE_UNKNOWN") == 0 
             || state.compareTo("JOB_STATE_STOPPED") == 0
             || state.compareTo("JOB_STATE_RUNNING") == 0
             || state.compareTo("JOB_STATE_DRAINING") == 0
             || state.compareTo("JOB_STATE_PENDING") == 0
             || state.compareTo("JOB_STATE_CANCELLING") == 0
             || state.compareTo("JOB_STATE_QUEUED") == 0) {
        updatedJob =  new RunningJob(projectId, fetchJobInfo.getId(), fetchJobInfo.getState(), 
                          fetchJobInfo.getStateTime(), fetchJobInfo.getRegion(), fetchJobInfo.getType());
      } else {
        updatedJob = new FinalisedJob(projectId, fetchJobInfo.getId(), fetchJobInfo.getState(),
                          fetchJobInfo.getStateTime(), fetchJobInfo.getRegion(), fetchJobInfo.getType());
      }

      fetchJobInfo.setMetrics(updatedJob, lastModified);

      return updatedJob;
    }
}
