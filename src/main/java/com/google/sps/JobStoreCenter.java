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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.Clock;
import java.util.List;
import java.util.ArrayList;
import com.google.sps.data.JobJSON;

// Class that deals with the interaction with Datastore 
public final class JobStoreCenter {
  DatastoreService datastore;

  public JobStoreCenter() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  public void addNewProject(String projectId, String pathToJsonFile) throws IOException , GeneralSecurityException {
    ProjectCenter projectCenter = new ProjectCenter(projectId, pathToJsonFile);

    Entity project = new Entity("Project", projectId);
    project.setProperty("projectId", projectId);
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
    jobEntity.setProperty("jobId", job.id);
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

  // Gets all jobs associated with a projectId
  public List<JobJSON> getJobsFromDatastore(String projectId) {
      List<JobJSON> jobs = new ArrayList<>();

      Key projectKey = KeyFactory.createKey("Project", projectId);
      // By default, ancestor queries include the specified ancestor itself.
      // The following filter excludes the ancestor from the query results.
      Filter keyFilter =
        new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.GREATER_THAN, projectKey);
      
      // If I want to query for specific type of Jobs (Running, Finalised), 
      // give Query constructor the name of the class
      Query queryJobs = new Query().setAncestor(projectKey).setFilter(keyFilter);

      PreparedQuery resultsJobs = datastore.prepare(queryJobs);

      
      for (Entity entity : resultsJobs.asIterable()) {
        String name = (String) entity.getProperty("name");
        String id = (String) entity.getProperty("jobId");
        String type = (String) entity.getProperty("type");
        String sdk = (String) entity.getProperty("sdk");
        String sdkSupportStatus = (String) entity.getProperty("sdkSupportStatus");
        String region = (String) entity.getProperty("region");
        int currentWorkers = ((Long) entity.getProperty("currentWorkers")).intValue();
        String startTime = (String) entity.getProperty("startTime");
        Double totalVCPUTime = (Double) entity.getProperty("totalVCPUTime"); 
        Double totalMemoryTime = (Double) entity.getProperty("totalMemoryTime"); 
        Double totalDiskTimeHDD = (Double) entity.getProperty("totalDiskTimeHDD");
        Double totalDiskTimeSSD = (Double) entity.getProperty("totalDiskTimeSSD");
        Long longCurrentVcouCount = (Long) entity.getProperty("currentVcpuCount");
        Integer currentVcpuCount = longCurrentVcouCount == null? null : longCurrentVcouCount.intValue();
        Double totalStreamingData = (Double) entity.getProperty("totalStreamingData"); 
        Boolean enableStreamingEngine = (Boolean) entity.getProperty("enableStreamingEngine");
        String metricTime = (String) entity.getProperty("metricTime");
        String state = (String) entity.getProperty("state");
        String stateTime = (String) entity.getProperty("stateTime");

        JobJSON job = new JobJSON(projectId, name, id, type, sdk, sdkSupportStatus, region, 
                                     currentWorkers, startTime, totalVCPUTime, totalMemoryTime,
                                        totalDiskTimeHDD, totalDiskTimeSSD, currentVcpuCount,
                                            totalStreamingData, enableStreamingEngine, metricTime,
                                                state, stateTime);
        
        jobs.add(job);
      }

      return jobs;
  }
}