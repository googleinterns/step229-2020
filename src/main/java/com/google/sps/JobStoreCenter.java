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
  DatastoreService datastore;
  MyClock clock;

  public JobStoreCenter(MyClock clock) {
    datastore = DatastoreServiceFactory.getDatastoreService();
    this.clock = clock;
  }

  private void addNewProject(String projectId, ProjectLoader projectLoader) throws IOException {
    Entity project = new Entity("Project", projectId);
    project.setProperty("projectId", projectId);
    project.setProperty("lastAccessed", clock.getCurrentTime());
    datastore.put(project);

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
    Integer currentVcpuCount = null;
    if (entity.getProperty("currentVcpuCount") != null) {
      currentVcpuCount = new Integer(entity.getProperty("currentVcpuCount").toString());
    }
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
        project = datastore.get(projectKey);
      } catch (EntityNotFoundException e) {
        System.out.println("JobNotFound");
        return null;
      }

      return project;
  }
  
  // Updates a project. The project must already exist 
  private void updateProject(String projectId, ProjectLoader projectLoader, Entity project) throws IOException {
    String lastTimeAccessedString = (String) project.getProperty("lastAccessed");
    Instant lastTimeAccessed = Instant.parse(lastTimeAccessedString);

    project.setProperty("lastAccessed", clock.getCurrentTime());
    datastore.put(project);

    List<JobModel> allJobs = projectLoader.fetchJobsforUpdate(lastTimeAccessedString);
    for (JobModel job : allJobs) {
      String time = job.stateTime;
      Instant stateTime = Instant.parse(time);
      
      // The state was changed after our last fetch
      if (stateTime.compareTo(lastTimeAccessed) > 0) {
        // A FinalisedJob can't have its state modified, so the job
        // must have Running the last time the Datastore was updated

        if (job instanceof RunningJob) {
          updateJobToDatastore(job, projectId, project, projectLoader);
        } else {
          // Delete the RunningJob job in DataStore
          Key k =
              new KeyFactory.Builder("Project", projectId)
                .addChild("RunningJob", job.id)
                .getKey();
          datastore.delete(k);

          // Fetch the job once again
          JobModel updatedJob = projectLoader.fetch(job.id, job.region);
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
      jobEntity = datastore.get(k);

      jobEntity.setProperty("state", updatedJob.state);
      jobEntity.setProperty("stateTime", updatedJob.stateTime);

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
      if (updatedJob.metricTime != null) {
        jobEntity.setProperty("metricTime", updatedJob.metricTime);
      }

      PriceCenter priceCenter = new PriceCenter();
      jobEntity.setProperty("price", priceCenter.calculatePrice(convertEntityToJobJSON(jobEntity)));

      datastore.put(jobEntity);

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
  public void dealWithProject(String projectId, ProjectLoader projectLoader) throws IOException {
    Entity project = fetchProject(projectId);

    if (project == null) {
      addNewProject(projectId, projectLoader);
    } else {
      updateProject(projectId, projectLoader, project);
    }
  }
}