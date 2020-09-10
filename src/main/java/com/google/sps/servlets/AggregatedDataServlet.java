// Copyright 2020 Google LLC

/**
 * @author tblanshard
 */

package com.google.sps.servlets;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import java.lang.Exception;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.sps.data.JobJSON;
import java.util.List;
import java.util.Map;
import com.google.sps.AggregationCenter;
import com.google.sps.JobStoreCenter;
import com.google.sps.DatastoreInteraction;

// Returns jobs aggregated into a map based on the criteria
// specified by the user
@WebServlet("/get-aggregated-data")
public class AggregatedDataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String projectID = request.getParameter("projectID");
    String option = request.getParameter("option");
    
    DatastoreInteraction datastore = new DatastoreInteraction();
    JobStoreCenter jobCenter = new JobStoreCenter(datastore);

    List<JobJSON> allJobs = jobCenter.getJobsFromDatastore(projectID);
    
    AggregationCenter aggregationCenter = new AggregationCenter();

    Map<String, List<JobJSON>> aggregatedJobs = null;

    if (option.compareTo("jobName") == 0) {
      aggregatedJobs = aggregationCenter.aggregateByName(allJobs);
    } else if (option.compareTo("owner") == 0) {
      aggregatedJobs = aggregationCenter.aggregateByUser(allJobs);
    } else if (option.compareTo("region") == 0) {
      aggregatedJobs = aggregationCenter.aggregateByRegion(allJobs);
    } else if (option.compareTo("programmingLanguage") == 0) {
      aggregatedJobs = aggregationCenter.aggregateByProgrammingLanguage(allJobs);
    } else if (option.compareTo("sdk") == 0) {
      aggregatedJobs = aggregationCenter.aggregateBySDKSupportStatus(allJobs);
    }

    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(aggregatedJobs));
  }
}