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
  private List<JobJSON> allJobs;
  private List<JobJSON> allUpdatedJobs;
  private String projectId;
  private FetchJobStub fetchJobs = new FetchJobStub();

  public ProjectLoaderStub(Map<String, List<JobJSON>> mapOfJobs, String projectId, 
                           List<JobJSON> allJobs, List<JobJSON> allUpdatedJobs) {
    this.mapOfJobs = mapOfJobs;
    this.projectId = projectId;
    this.allJobs = allJobs;
    this.allUpdatedJobs = allUpdatedJobs;
  }

  public ProjectLoaderStub(String projectId) {
    mapOfJobs = new HashMap<>();
    this.projectId = projectId;
    allJobs = new ArrayList<>();
    allUpdatedJobs = new ArrayList<>();
  }

  public List<JobModel> fetchJobs() throws IOException {
    List<JobModel> jobs = new ArrayList<>();

    for (JobJSON job : allJobs) {
      fetchJobs.setJob(job);
      jobs.add(JobModel.createJob(projectId, fetchJobs)); 
    }

    return jobs;
  }
  // Fetch a job from a project, based on the jobId
  public JobModel fetch(String jobId) throws IOException {
    Iterator<String> it = mapOfJobs.keySet().iterator();

    while (it.hasNext()) {
      String region = it.next();

      for (JobJSON job : mapOfJobs.get(region)) {
        if (jobId.compareTo(job.id) == 0) {
          fetchJobs.setJob(job);
          return JobModel.createJob(projectId, fetchJobs); 
        } 
      }
    }

    return null;  
  }

  //Fetch a job from a project based on the job Id and its location
  public JobModel fetch(String jobId, String location) throws IOException {
    if (mapOfJobs.containsKey(location)) {
      for (JobJSON job : mapOfJobs.get(location)) {
        if (jobId.compareTo(job.id) == 0) {
          fetchJobs.setJob(job);
          return JobModel.createJob(projectId, fetchJobs); 
        } 
      } 
    }

    return null; 
  }

  public void setUpdatedJobs(List<JobJSON> allUpdatedJobs) {
    this.allUpdatedJobs = allUpdatedJobs;
  }

  // Jobs have the metrics that were modified since lastUpdate different from null
  public List<JobModel> fetchJobsforUpdate(String lastUpdate) throws IOException {
    List<JobModel> jobs = new ArrayList<>();

    for (JobJSON job : allUpdatedJobs) {
      fetchJobs.setJob(job);
      jobs.add(JobModel.createJob(projectId, fetchJobs));  
    }

    return jobs;
  }
}