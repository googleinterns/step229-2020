// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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


public final class ProjectCenter {
    private String projectId;
    GoogleCredential credential;

    public ProjectCenter(String projectId) throws IOException {
      this.projectId = projectId;
        
      credential = GoogleCredential.fromStream(new FileInputStream("bt-dataflow-sql-demo.json"));
      if (credential.createScopedRequired()) {
        credential = credential.createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));
      }
    }

    // Fetches all jobs in a project and returns a list with all of them
    public List<Job> fetchJobs() throws IOException, GeneralSecurityException {
      HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
      Dataflow dataflowService = new Dataflow.Builder(httpTransport, jsonFactory, credential)
      .setApplicationName("Google Cloud Platform Sample")
      .build();
      
      Dataflow.Projects.Jobs.Aggregated request = dataflowService.projects().jobs().aggregated(projectId);
      ListJobsResponse response;
      ArrayList<Job> jobs = new ArrayList<>();
      do {
        response = request.execute();
        
        for (Job job : response.getJobs()) {
          jobs.add(job);
        }
        
        request.setPageToken(response.getNextPageToken());
      } while (response.getNextPageToken() != null);

      return jobs;
    }
}