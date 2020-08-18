// Copyright 2020 Google LLC

/**
 * @author tblanshard
 */

package com.google.sps.servlets;

import java.io.IOException;
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
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.iam.v1.Iam;
import com.google.api.services.iam.v1.model.Policy;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@WebServlet("/check-permissions")
public class CheckPermissionsServlet extends HttpServlet {
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String projectId = request.getParameter("projID");
    String serviceAccountID = request.getParameter("saID");
    String jsonPath = projectId + "-key.json";
    String resource = "projects/" + projectId + "/serviceAccounts/" + serviceAccountID;

  // Lists all service accounts for the current project.

    Iam service = null;
    try {
      service = createIAMService(jsonPath);
    } catch (IOException | GeneralSecurityException e) {
      System.out.println("Unable to initialize service: \n" + e.toString());
      return;
    }

    try {
      Iam.Projects.ServiceAccounts.GetIamPolicy policyRequest = 
          service.projects().serviceAccounts().getIamPolicy(resource).setAlt("json");
          
      Policy policyResponse = policyRequest.execute();
      System.out.println(policyResponse);

      response.setContentType("application/json;");
      Gson gson = new Gson();
      response.getWriter().println(gson.toJson(policyResponse));

    } catch (IOException e) {
      System.out.println("Unable to return policy \n" + e.toString());
    }
  }

  private static Iam createIAMService(String jsonPath) throws GeneralSecurityException, IOException {
    // uses the API key as a json file.
    GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(jsonPath));
    if (credentials.createScopedRequired()) {
      credentials = credentials.createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));
    }

    // Initialize the IAM service, which can be used to send requests to the IAM API.
    Iam service =
        new Iam.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
            .setApplicationName("service-account-policy")
            .build();
    return service;
  }
}
