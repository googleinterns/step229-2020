// Copyright 2020 Google LLC

/**
 * @author andreeanica
 */

package com.google.sps;

import java.util.*;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import java.io.FileInputStream;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.dataflow.Dataflow;
import com.google.api.services.dataflow.model.Job;
import com.google.api.services.dataflow.model.ListJobsResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.sps.data.JobModel;

public final class ProjectCenter {
    private static List<String> regions = Arrays.asList("us-west1",
                                                         "us-central1",
                                                         "us-east1",
                                                         "us-east4",
                                                         "northamerica-northeast1",
                                                         "europe-west2",
                                                         "europe-west1",
                                                         "europe-west4",
                                                         "europe-west3",
                                                         "asia-southeast1",
                                                         "asia-east1",
                                                         "asia-northeast1",
                                                         "australia-southeast1");
    private String projectId;
    private String pathToJsonFile;
    private GoogleCredential credential;
    private Dataflow dataflowService;

    public ProjectCenter(String projectId, String pathToJsonFile) throws IOException,  GeneralSecurityException {
      this.projectId = projectId;
      this.pathToJsonFile = pathToJsonFile;
        
      credential = GoogleCredential.fromStream(new FileInputStream(pathToJsonFile));
      if (credential.createScopedRequired()) {
        credential = credential.createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));
      }

      HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
      dataflowService = new Dataflow.Builder(httpTransport, jsonFactory, credential)
      .setApplicationName("Google Cloud Platform Sample")
      .build();
    }

    // Fetches all jobs in a project and returns a list with all of them
    public List<JobModel> fetchJobs() throws IOException {

      Dataflow.Projects.Jobs.Aggregated request = dataflowService.projects().jobs().aggregated(projectId);
      ListJobsResponse response;
      ArrayList<JobModel> jobs = new ArrayList<>();
      do {
        response = request.execute();
        
        for (Job job : response.getJobs()) {
          jobs.add(JobModel.createJob(projectId, job, dataflowService));
        }
        
        request.setPageToken(response.getNextPageToken());
      } while (response.getNextPageToken() != null);

      return jobs;
    }

    public JobModel fetch(String jobId) throws IOException {
      for (String region : regions) {      
        Dataflow.Projects.Locations.Jobs.Get request = dataflowService.projects()
        .locations()
        .jobs()
        .get(projectId, region, jobId);

        Job job;

        try {
            job = request.execute();
        } catch (GoogleJsonResponseException e) {
            // If we receive an error Message, we try further other regions
            continue;
        }

        // If the request was successful
        if (request.getLastStatusCode() >= 200 && request.getLastStatusCode() <  300) {
          if (job != null) {
            return JobModel.createJob(projectId, job, dataflowService);
          }
        }  
      }

      return null;
    }

    public JobModel fetch(String jobId, String location) throws IOException {     
      Dataflow.Projects.Locations.Jobs.Get request = dataflowService.projects()
      .locations()
      .jobs()
      .get(projectId, location, jobId);

      Job job;

      try {
        job = request.execute();
      } catch (GoogleJsonResponseException e) {
        return null;
      }

      if (job != null) {
        return JobModel.createJob(projectId, job, dataflowService);
      }

      return null;
    }
}