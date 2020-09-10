// Copyright 2020 Google LLC

/**
 * @author andreeanica
 */

package com.google.sps;

import java.util.List;
import com.google.sps.data.JobModel;
import java.io.IOException;

// Interface designed to separate working with the server to fetch a project
// from the actual implementation. Makes it easier to test, by allowing creating
// mock objects
public interface ProjectLoader {
  public List<JobModel> fetchJobs() throws IOException;
  // Fetch a job from a project, based on the jobId
  public JobModel fetch(String jobId) throws IOException;
  //Fetch a job from a project based on the job Id and its location
  public JobModel fetch(String jobId, String location) throws IOException;
  // Jobs have the metrics that were modified sincer lastUpdate different from null
  public List<JobModel> fetchJobsforUpdate(String lastUpdate) throws IOException;
}