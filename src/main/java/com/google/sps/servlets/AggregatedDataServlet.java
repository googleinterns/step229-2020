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

@WebServlet("/get-aggregated-data")
public class AggregatedDataServlet extends HttpServlet {
  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    /*String method = getParameter(request, "aggregationMethod", "");
    AggregationCenter aggretationService = new AggregationCenter();
    Map<String, List<JobJSON>> aggregatedJobs;
    if (method.compareTo("jobName") == 0) {
      //needs a list of jobs being passed in
      aggregatedJobs = aggretationService.aggregateByName();
    } else if (method.compareTo("owner") == 0) {
      //needs a list of jobs being passed in
      aggregatedJobs = aggretationService.aggregateByUser();
    } else {
      
    }
    response.setContentType("application/json;");
    Gson gson = new Gson();
    response.getWriter().println(gson.toJson(aggregatedJobs));*/
  }

  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}