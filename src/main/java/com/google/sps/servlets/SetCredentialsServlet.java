// Copyright 2020 Google LLC

/**
 * @author tblanshard
 */

package com.google.sps.servlets;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import java.lang.Exception;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import static java.nio.charset.StandardCharsets.UTF_8;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.ReadChannel;

import com.google.gson.Gson;
import java.security.GeneralSecurityException;

import org.json.JSONException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileOutputStream;

import com.google.api.services.storage.*;
import com.google.api.client.http.*;
import com.google.api.client.googleapis.javanet.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.File;

import java.util.*;
import java.nio.file.attribute.*;
import java.nio.file.*;

@WebServlet("/get-credentials")
public class SetCredentialsServlet extends HttpServlet {
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String bucketName = request.getParameter("bucket");
    String objectName = request.getParameter("object");
    String projectId = request.getParameter("projID");

    Boolean getCredentials = false;
    Gson gson = new Gson();
    response.setContentType("application/json;");

    try{
      Storage storage = StorageOptions.newBuilder()
                  .setProjectId(projectId)
                  .setCredentials(GoogleCredentials.getApplicationDefault())
                  .build()
                  .getService();

      Blob blob = storage.get(bucketName, objectName);

      String fileContent = new String(blob.getContent());

      //File file = new File("pom.xml");
      String pathToJson = System.getProperty("java.io.tmpdir") + "/" + projectId + ".json";

      //if the file exists, delete it to make sure we have an up-to-date version
      File apiFile = new File(pathToJson);
      boolean exists = apiFile.exists();
      if (exists) {
        apiFile.delete();
      }

      //create file and give it read/write permissions
      Set<PosixFilePermission> ownerWritable = PosixFilePermissions.fromString("rw-rw-rw-");
      FileAttribute<?> permissions = PosixFilePermissions.asFileAttribute(ownerWritable);
      Files.createFile(Paths.get(pathToJson), permissions);

      PrintWriter out = new PrintWriter(new FileWriter(pathToJson));

      out.println(fileContent);
      out.flush();
      out.close();
      
      getCredentials = true;
      response.getWriter().println(gson.toJson(getCredentials));
    } catch (Exception e) {
      response.getWriter().println(gson.toJson(getCredentials));
    }
  }
}