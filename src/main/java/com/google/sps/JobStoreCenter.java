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
import com.google.sps.data.Project;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.api.services.dataflow.model.Job;

// Class that deals with the interaction with Datastore 
public final class JobStoreCenter {
  DatabaseInteraction database;

  public JobStoreCenter(DatabaseInteraction database) {
    this.database = database;
  }

  public void addNewProject(String projectId, ProjectLoader projectLoader) throws IOException , GeneralSecurityException {
    Entity project = new Entity("Project", projectId);
    project.setProperty("projectId", projectId);
    project.setProperty("lastAccessed", java.time.Clock.systemUTC().instant().toString());
    database.put(project);

    List<JobModel> allJobs = projectLoader.fetchJobs();
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
    jobEntity.setProperty("currentMemoryUsage", job.currentMemoryUsage);
    jobEntity.setProperty("currentPdUsage", job.currentPdUsage);
    jobEntity.setProperty("currentSsdUsage", job.currentSsdUsage);
    jobEntity.setProperty("metricTime", job.metricTime);
    jobEntity.setProperty("state", job.state);
    jobEntity.setProperty("stateTime", job.stateTime);
    jobEntity.setProperty("sdkName", job.sdkName);

    PriceCenter priceCenter = new PriceCenter();
    jobEntity.setProperty("price", priceCenter.calculatePrice(job));

    database.put(jobEntity);
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

    for (Entity entity : database.getJobsFromProject(projectKey)) {
      JobJSON job = convertEntityToJobJSON(entity);
      jobs.add(job);
    }

    return jobs;
  }

  private JobJSON convertEntityToJobJSON(Entity entity) {
    String projectId = (String) entity.getProperty("projectId");
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
    Long longCurrentVcpuCount = (Long) entity.getProperty("currentVcpuCount");
    Integer currentVcpuCount = longCurrentVcpuCount == null? null : longCurrentVcpuCount.intValue();
    Double totalStreamingData = (Double) entity.getProperty("totalStreamingData"); 
    Boolean enableStreamingEngine = (Boolean) entity.getProperty("enableStreamingEngine");
    Double currentMemoryUsage = (Double) entity.getProperty("currentMemoryUsage");
    Double currentPdUsage = (Double) entity.getProperty("currentPdUsage");
    Double currentSsdUsage = (Double) entity.getProperty("currentSsdUsage");
    String metricTime = (String) entity.getProperty("metricTime");
    String state = (String) entity.getProperty("state");
    String stateTime = (String) entity.getProperty("stateTime");
    Double price = (Double) entity.getProperty("price");
    String sdkName = (String) entity.getProperty("sdkName");

    JobJSON job = new JobJSON(projectId, name, id, type, sdk, sdkSupportStatus, region, 
                                  currentWorkers, startTime, totalVCPUTime, totalMemoryTime,
                                      totalDiskTimeHDD, totalDiskTimeSSD, currentVcpuCount,
                                          totalStreamingData, enableStreamingEngine, metricTime,
                                              state, stateTime, price, sdkName, currentMemoryUsage,
                                                  currentPdUsage, currentSsdUsage);
    return job;
  }

  public Entity fetchProject(String projectId)  {
      Entity project = null;
      Key projectKey = KeyFactory.createKey("Project", projectId);
      try{
        project = database.get(projectKey);
      } catch (EntityNotFoundException e) {
        System.out.println("JobNotFound");
        return null;
      }

      return project;
  }
  
  // Updates a project. The project must already exist 
  public void updateProject(String projectId, ProjectLoader projectLoader, Entity project) throws IOException , GeneralSecurityException {
    String lastTimeAccessedString = (String) project.getProperty("lastAccessed");
    Instant lastTimeAccessed = Instant.parse(lastTimeAccessedString);

    project.setProperty("lastAccessed", java.time.Clock.systemUTC().instant().toString());
    database.put(project);

    List<JobModel> allJobs = projectLoader.fetchJobsforUpdate(lastTimeAccessedString);
    for (JobModel job : allJobs) {
      String time = job.stateTime;
      Instant stateTime = Instant.parse(time);
      
      // The state was changed after our last fetch
      if (stateTime.compareTo(lastTimeAccessed) > 0) {
        // A FinalisedJob can't have its state modified, so the job
        // must have Running the last time the Datastore was updated

        // Delete the RunningJob job in DataStore
        Key k =
            new KeyFactory.Builder("Project", projectId)
              .addChild("RunningJob", job.id)
              .getKey();
        database.delete(k);

        // Add the new Job
        JobModel updatedJob = projectLoader.fetch(job.id, job.region);
        if (updatedJob instanceof RunningJob) {
          addJobToDatastore((RunningJob)updatedJob, project);
        } else {
          addJobToDatastore((FinalisedJob)updatedJob, project);
        }
      } else {
        if (job.updated) {
          updateJobToDatastore(job, projectId, project, projectLoader);
        }
      }
    }     
  }
 
 // Changes only the fields that were modified
  private void updateJobToDatastore (JobModel updatedJob, String projectId, Entity project, 
      ProjectLoader projectLoader) throws IOException {
    // Only Running Jobs can be updated
    Key k =
        new KeyFactory.Builder("Project", projectId)
            .addChild("RunningJob", updatedJob.id)
            .getKey();
    Entity jobEntity;
    try{
      jobEntity = database.get(k);  
      if (updatedJob.totalVCPUTime != null) {
        jobEntity.setProperty("totalVCPUTime", updatedJob.totalVCPUTime);
      }
      if (updatedJob.totalMemoryTime != null) {
        jobEntity.setProperty("totalMemoryTime", updatedJob.totalMemoryTime);
      }
      if (updatedJob.totalDiskTimeHDD != null) {
        jobEntity.setProperty("totalDiskTimeHDD", updatedJob.totalDiskTimeHDD);
      }
      if (updatedJob.totalDiskTimeSSD != null) {
        jobEntity.setProperty("totalDiskTimeSSD", updatedJob.totalDiskTimeSSD);
      }
      if (updatedJob.currentVcpuCount != null) {
        jobEntity.setProperty("currentVcpuCount", updatedJob.currentVcpuCount);
      }
      if (updatedJob.totalStreamingData != null) {
        jobEntity.setProperty("totalStreamingData", updatedJob.totalStreamingData);
      }
      if (updatedJob.enableStreamingEngine != null) {
        jobEntity.setProperty("enableStreamingEngine", updatedJob.enableStreamingEngine);
      }
      if (updatedJob.currentMemoryUsage != null) {
        jobEntity.setProperty("currentMemoryUsage", updatedJob.currentMemoryUsage);
      }
      if (updatedJob.currentPdUsage != null) {
        jobEntity.setProperty("currentPdUsage", updatedJob.currentPdUsage);
      }
      if (updatedJob.currentSsdUsage != null) {
        jobEntity.setProperty("currentSsdUsage", updatedJob.currentSsdUsage);
      }

      PriceCenter priceCenter = new PriceCenter();
      jobEntity.setProperty("price", priceCenter.calculatePrice(convertEntityToJobJSON(jobEntity)));

      database.put(jobEntity);

      } catch (EntityNotFoundException e) {
        // The Job doesn't exist
        JobModel newjob = projectLoader.fetch(updatedJob.id, updatedJob.region);
        if (updatedJob instanceof RunningJob) {
          addJobToDatastore((RunningJob)updatedJob, project);
        } else {
          addJobToDatastore((FinalisedJob)updatedJob, project);
        }
      }
  }
  
  // Function that creates the new project in Datastore if the project doesn t already
  // exist or updates the existing project
  public void dealWithProject(String projectId, ProjectLoader projectLoader) throws IOException , GeneralSecurityException {
    Entity project = fetchProject(projectId);

    if (project == null) {
      addNewProject(projectId, projectLoader);
    } else {
      updateProject(projectId, projectLoader, project);
    }
  }
}