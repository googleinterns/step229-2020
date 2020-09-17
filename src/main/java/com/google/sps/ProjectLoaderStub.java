// Copyright 2020 Google LLC

/**
 * @author andreeanica
 */

package com.google.sps;

import java.util.List;
import com.google.sps.data.JobModel;
import java.io.IOException;
import java.util.Map;
import java.util.ArrayList;
import java.util.Iterator;
import com.google.sps.data.JobJSON;
import com.google.sps.data.FetchJobStub;
import java.util.HashMap;

public class ProjectLoaderStub implements ProjectLoader {
  private Map<String, List<JobJSON>> mapOfJobs;
  private Map<String, JobJSON> allJobs;

  // Jobs that are used to tell which fields have been modified or not
  // They shoud be jobs with the same jobId as the jobs found in updatedJobs
  private Map<JobJSON, Boolean> updateHelpersJobs = null;
  // Updated Jobs. All jobs included in this list should have one associated helper
  // found in updateHelpersJobs
  private List<JobJSON> updatedJobs = null;

  private String projectId;
  private FetchJobStub fetchJobs = new FetchJobStub();

  public ProjectLoaderStub(Map<String, List<JobJSON>> mapOfJobs, String projectId) {
    this(projectId);

    this.mapOfJobs = mapOfJobs;
    this.projectId = projectId;

    Iterator<String> it = this.mapOfJobs.keySet().iterator();

    while (it.hasNext()) {
      String region = it.next();
      for (JobJSON job : mapOfJobs.get(region)) {
        allJobs.put(job.id, job);
      }
    }
  }

  public ProjectLoaderStub(String projectId) {
    this.projectId = projectId;
    allJobs = new HashMap<>();
    mapOfJobs = new HashMap<>();
  }

  public List<JobModel> fetchJobs() throws IOException {
    List<JobModel> jobs = new ArrayList<>();

    for (String id : allJobs.keySet()) {
      JobJSON job = allJobs.get(id);
      
      fetchJobs.setJob(job);
      jobs.add(JobModel.createJob(projectId, fetchJobs)); 
    }

    return jobs;
  }
  // Fetch a job from a project, based on the jobId
  public JobModel fetch(String jobId) throws IOException {
    if (allJobs.containsKey(jobId)) {
      JobJSON job = allJobs.get(jobId);
      fetchJobs.setJob(job);
      return JobModel.createJob(projectId, fetchJobs);
    }

    return null;  
  }

  //Fetch a job from a project based on the job Id and its location
  public JobModel fetch(String jobId, String location) throws IOException {
    if (allJobs.containsKey(jobId)) {
      JobJSON job = allJobs.get(jobId);
      if (job.region.compareTo(location) == 0) {
        fetchJobs.setJob(job);
        return JobModel.createJob(projectId, fetchJobs);
      }
    }

    return null; 
  }

  public void setUpdate(Map<JobJSON, Boolean> updateHelpersJobs, List<JobJSON> updatedJobs) {
    this.updateHelpersJobs = updateHelpersJobs;
    this.updatedJobs = updatedJobs;

    for (JobJSON job : updatedJobs) {
      allJobs.replace(job.id, job);
    }
  }

  // Jobs have the metrics that were modified since lastUpdate different from null
  public List<JobModel> fetchJobsforUpdate(String lastUpdate) throws IOException {

    if (updateHelpersJobs == null) {
      return new ArrayList<JobModel>();
    }

    List<JobModel> jobs = new ArrayList<>();

    for (JobJSON job : updateHelpersJobs.keySet()) {
      fetchJobs.setIfUpdated(updateHelpersJobs.get(job));
      fetchJobs.setJob(job);
      jobs.add(JobModel.createJob(projectId, fetchJobs));  
    }

    fetchJobs.setIfUpdated(false);

    return jobs;
  }
}