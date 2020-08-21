// Copyright 2020 Google LLC

/**
 * @author andreeanica
 */

package com.google.sps.servlets;

import java.util.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.JobStoreCenter;
import java.security.GeneralSecurityException;
import java.io.File;
import com.google.sps.data.AccessRequest;
import com.google.sps.data.JobJSON;

@WebServlet("/jobs")
public class JobsServlet extends HttpServlet {
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    JobStoreCenter jobCenter = new JobStoreCenter();

    String projectID = request.getParameter("projectID");
    List<JobJSON> jobs = jobCenter.getJobsFromDatastore(projectID);

    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(jobs));
  }

  @Override
  // Adds a new project to  the Datastore
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      Gson gson = new Gson();

      // Convert the JSON to an instance of MeetingRequest.
      AccessRequest accessRequest = gson.fromJson(request.getReader(), AccessRequest.class);

      String projectId = accessRequest.projectID;
      String pathToJsonFile = accessRequest.pathToJsonFile;
      
      JobStoreCenter jobCenter = new JobStoreCenter();

      try{
        jobCenter.addNewProject(projectId, pathToJsonFile);
      } catch (GeneralSecurityException e) {
        System.out.println("Unable to initialize service: \n" + e.toString());
        return;
      }
      
      response.sendRedirect("/index.html");
  }
}