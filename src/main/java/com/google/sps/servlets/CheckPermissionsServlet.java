// Copyright 2020 Google LLC

/**
 * @author tblanshard
 */

package com.google.sps.servlets;

//import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.cloudresourcemanager.CloudResourceManager;
import com.google.api.services.cloudresourcemanager.model.Project;
import com.google.api.services.cloudresourcemanager.model.TestIamPermissionsRequest;
import com.google.api.services.cloudresourcemanager.model.TestIamPermissionsResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.net.MalformedURLException;
import java.net.URL;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.common.collect.Lists;
import java.io.FileInputStream;
import com.google.api.services.iam.v1.IamScopes;
//import com.google.api.services.iam.v1.Iam;
//import com.google.api.services.iam.v1.model.Policy;
import com.google.auth.http.HttpCredentialsAdapter;
import java.util.Collections;
import java.util.List;
import java.util.*;
import java.io.*;
import java.net.URL;

@WebServlet("/check-permissions")
public class CheckPermissionsServlet extends HttpServlet {
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String projectId = request.getParameter("projID");

    File file = new File(projectId + ".json");
    String jsonPath = file.getAbsolutePath();

    //Tests the required permissions for the service account.

    TestIamPermissionsRequest requestBody = new TestIamPermissionsRequest();

    CloudResourceManager cloudResourceManagerService = null;

    response.setContentType("application/json");
    Gson gson = new Gson();

    try {
      cloudResourceManagerService = createCloudResourceManagerService(jsonPath);
    } catch (IOException | GeneralSecurityException e) {
      System.out.println("Unable to initialize service: \n" + e.toString());
      response.getWriter().println(gson.toJson("Please check the project ID."));
      return;
    }

    List<String> requiredPermissions =
      Arrays.asList("dataflow.jobs.get",
        "dataflow.jobs.list",
        "dataflow.messages.list",
        "dataflow.metrics.get",
        "dataflow.snapshots.get",
        "dataflow.snapshots.list");
        //"resourcemanager.projects.get",
        //"resourcemanager.projects.list");

    try {
      TestIamPermissionsRequest permissionsRequestBody = 
        new TestIamPermissionsRequest().setPermissions(requiredPermissions);

      CloudResourceManager.Projects.TestIamPermissions testingPermissionsResponse = 
        cloudResourceManagerService.projects().testIamPermissions(projectId, permissionsRequestBody);

      TestIamPermissionsResponse permissionsResponse = testingPermissionsResponse.execute();

      //check if any permissions have been returned
      if (permissionsResponse.getPermissions() == null){
        response.getWriter().println(gson.toJson("No permissions have been assigned to the service account."));
        return;
      }

      int missing = requiredPermissions.size() - permissionsResponse.getPermissions().size();
      Boolean arePermissionsCorrect = missing == 0;

      Set<String> required = new HashSet<>(requiredPermissions);
      required.removeAll(permissionsResponse.getPermissions());
      response.getWriter().println(gson.toJson(Arrays.asList(required, missing)));

    } catch (IOException e) {
      System.out.println("Unable to return permissions \n" + e.toString());
      response.getWriter().println(gson.toJson("Unable to test permissions."));
    }
  }

  private static CloudResourceManager createCloudResourceManagerService(String jsonPath) throws GeneralSecurityException, IOException {
    // uses the API key as a json file.
    GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(jsonPath));
    if (credentials.createScopedRequired()) {
      credentials = credentials.createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));
    }

    // Initialize the IAM service, which can be used to send requests to the IAM API.
    CloudResourceManager service =
        new CloudResourceManager.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
            .setApplicationName("service-account-policy")
            .build();
    return service;
  }
}
