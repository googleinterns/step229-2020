// Copyright 2020 Google LLC

/**
 * @author andreeanica
 */

package com.google.sps;

import com.google.sps.data.JobModel;
import com.google.sps.data.FinalisedJob;
import com.google.sps.data.RunningJob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.Clock;
import java.util.List;

// Class that deals with the interaction with Datastore 
public final class JobStoreCenter {
  DatastoreService datastore;

  public JobStoreCenter() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  public void addNewProject(String projectId, String pathToJsonFile) throws IOException , GeneralSecurityException {
    ProjectCenter projectCenter = new ProjectCenter(projectId, pathToJsonFile);

    Entity project = new Entity("Project", projectId);
    project.setProperty("lastAccessed", java.time.Clock.systemUTC().instant().toString());
    datastore.put(project);

    List<JobModel> allJobs = projectCenter.fetchJobs();
    for (JobModel job : allJobs) {
        if (job instanceof RunningJob) {
          addJobToDatastore((RunningJob)job, project);
        } else {
          addJobToDatastore((FinalisedJob)job, project);
        }
    }    
  }

  private void addJobToDatastore(JobModel job, String jobClass, Entity project) {

    Entity jobEntity = new Entity(jobClass, job.id, project.getKey());
    jobEntity.setProperty("projectId", job.projectId);
    jobEntity.setProperty("name", job.name);
    jobEntity.setProperty("type", job.type);
    jobEntity.setProperty("sdk", job.sdk);
    jobEntity.setProperty("sdkSupportStatus", job.sdkSupportStatus);
    jobEntity.setProperty("region", job.region);
    jobEntity.setProperty("currentWorkers", job.currentWorkers);
    jobEntity.setProperty("startTime", job.startTime);
    jobEntity.setProperty("totalVCPUTime", job.totalVCPUTime);
    jobEntity.setProperty("totalMemoryTime", job.totalMemoryTime);
    jobEntity.setProperty("totalDiskTimeHDD", job.totalDiskTimeHDD);
    jobEntity.setProperty("totalDiskTimeSSD", job.totalDiskTimeSSD);
    jobEntity.setProperty("currentVcpuCount", job.currentVcpuCount);
    jobEntity.setProperty("totalStreamingData", job.totalStreamingData);
    jobEntity.setProperty("enableStreamingEngine", job.enableStreamingEngine);
    jobEntity.setProperty("metricTime", job.metricTime);
    jobEntity.setProperty("state", job.state);
    jobEntity.setProperty("stateTime", job.stateTime);

    datastore.put(jobEntity);
  }

  private void addJobToDatastore(FinalisedJob job, Entity project) {
      addJobToDatastore(job, "FinalisedJob", project);
  }

  private void addJobToDatastore(RunningJob job, Entity project) {
      addJobToDatastore(job, "RunningJob", project);
  }
}