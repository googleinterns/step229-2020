// Copyright 2020 Google LLC

/**
 * @author andreeanica
 */

package com.google.sps;

import org.junit.Ignore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.io.IOException;
import com.google.sps.data.JobJSON;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import com.google.appengine.api.datastore.EntityNotFoundException;

@RunWith(JUnit4.class)
public final class JobStoreCenterTest {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  private static JobStoreCenter jobStoreCenter;
  private static DatastoreService datastore;
  private static ClockStub clock;

  private static final String TIME1 = "2007-12-03T10:15:30.00Z";
  private static final String TIME2 = "2008-12-03T10:15:30.00Z";
  private static final String TIME3 = "2009-12-03T10:15:30.00Z";
  private static final String TIME4 = "2019-12-03T10:15:30.00Z";

  private static final String PROJECT1 = "project1";
  private static final String PROJECT2 = "project2";

  private static final JobJSON JOB1 = new JobJSON(PROJECT1, "job1", "job1", "JOB_TYPE_STREAMING", "2.23.0", "SUPPORTED",
                                                     "europe-west1", 0, TIME1, 100.0, 101.0, 102.0, 
                                                            103.0, 4, 105.0, true, TIME1, "JOB_STATE_RUNNING", TIME1, null, "Apache Beam SDK for Java",
                                                                201.0, 202.0, 203.0);
  private static final JobJSON JOB1_UPDATED_RUNNING = new JobJSON(PROJECT1, null, "job1", null, null, null,
                                                     "europe-west1", 0, null, 300.0, null, null, 
                                                            303.0, null, null, null, TIME3, "JOB_STATE_RUNNING", TIME1, null, null,
                                                                301.0, null, null);
  private static final JobJSON JOB1_RUNNING_AFTER_UPDATE = new JobJSON(PROJECT1, "job1", "job1", "JOB_TYPE_STREAMING", "2.23.0", "SUPPORTED",
                                                     "europe-west1", 0, TIME1, 300.0, 101.0, 102.0, 
                                                            303.0, 4, 105.0, true, TIME3, "JOB_STATE_RUNNING", TIME1, null, "Apache Beam SDK for Java",
                                                                301.0, 202.0, 203.0);
  private static final JobJSON JOB1_UPDATED_FINALISED = new JobJSON(PROJECT1, null, "job1", null, null, null,
                                                     "europe-west1", 0, null, 300.0, null, null, 
                                                            303.0, null, null, null, TIME3, "JOB_STATE_DONE", TIME3, null, null,
                                                                301.0, null, null);
  private static final JobJSON JOB1_FINALISED_AFTER_UPDATE = new JobJSON(PROJECT1, "job1", "job1", "JOB_TYPE_STREAMING", "2.23.0", "SUPPORTED",
                                                     "europe-west1", 0, TIME1, 300.0, 101.0, 102.0, 
                                                            303.0, 4, 105.0, true, TIME3, "JOB_STATE_DONE", TIME3, null, "Apache Beam SDK for Java",
                                                                301.0, 202.0, 203.0);
                    
  private static final JobJSON JOB2 = new JobJSON(PROJECT1, "job2", "job2", "JOB_TYPE_STREAMING", "2.23.0", "SUPPORTED",
                                                     "europe-west2", 0, null, 100.0, 101.0, 102.0, 
                                                            103.0, 4, 105.0, true, TIME1, "JOB_STATE_CANCELLED", TIME1, null, "Apache Beam SDK for Java",
                                                                201.0, 202.0, 203.0);
  private static final JobJSON JOB2_NO_UPDATE = new JobJSON(PROJECT1, null, "job2", null, null, null,
                                                     "europe-west2", 0, null, null, null, null, 
                                                            null, null, null, null, null, "JOB_STATE_CANCELLED", TIME1, null, null,
                                                                null, null, null);
  private static final JobJSON JOB3 = new JobJSON(PROJECT1, "job3", "job3", "JOB_TYPE_STREAMING", "2.23.0", "SUPPORTED",
                                                     "europe-west2", 0, null, 500.0, 501.0, 502.0, 
                                                            503.0, 4, 505.0, true, TIME1, "JOB_STATE_QUEUED", TIME1, null, "Apache Beam SDK for Java",
                                                                201.0, 202.0, 203.0);
  private static final JobJSON JOB4 = new JobJSON(PROJECT1, "job4", "job4", "JOB_TYPE_STREAMING", "2.23.0", "SUPPORTED",
                                                     "europe-west1", 0, null, 900.0, 501.0, 902.0, 
                                                            503.0, 4, 905.0, true, TIME1, "JOB_STATE_FAILED", TIME1, null, "Apache Beam SDK for Java",
                                                                201.0, 202.0, 203.0);

  private static final JobJSON JOB5 = new JobJSON(PROJECT2, "job1", "job1", "JOB_TYPE_STREAMING", "2.23.0", "SUPPORTED",
                                                     "europe-west1", 0, null, 900.0, 501.0, 902.0, 
                                                            503.0, 4, 905.0, true, TIME1, "JOB_STATE_FAILED", TIME1, null, "Apache Beam SDK for Java",
                                                                201.0, 202.0, 203.0);
  private static final JobJSON JOB6 = new JobJSON(PROJECT2, "job2", "job2", "JOB_TYPE_STREAMING", "2.23.0", "SUPPORTED",
                                                     "europe-west1", 0, TIME1, 100.0, 101.0, 102.0, 
                                                            103.0, 4, 105.0, true, TIME1, "JOB_STATE_RUNNING", TIME1, null, "Apache Beam SDK for Java",
                                                                201.0, 202.0, 203.0);

  @BeforeClass
  public static void initialiseVariables() {
    // Initialize the constants that would be used for testing

    PriceCenter priceCenter = new PriceCenter();
    JOB1_RUNNING_AFTER_UPDATE.price = priceCenter.calculatePrice(JOB1_RUNNING_AFTER_UPDATE);
    JOB1_FINALISED_AFTER_UPDATE.price = priceCenter.calculatePrice(JOB1_FINALISED_AFTER_UPDATE);
    JOB1.price = priceCenter.calculatePrice(JOB1);
    JOB2.price = priceCenter.calculatePrice(JOB2);
    JOB3.price = priceCenter.calculatePrice(JOB3);
    JOB4.price = priceCenter.calculatePrice(JOB4);
    JOB5.price = priceCenter.calculatePrice(JOB5);
    JOB6.price = priceCenter.calculatePrice(JOB6);
  }
  
  @Before
  public void setUp() {
    helper.setUp();
    clock = new ClockStub();
    jobStoreCenter = new JobStoreCenter(clock);

    datastore = DatastoreServiceFactory.getDatastoreService(); 
  }

  @After
  public void tearDown() {
    helper.tearDown() ;
  }

  @Test
  public void emptyProject() throws EntityNotFoundException, IOException {
    // Add only an empty project to the Database
    ProjectLoader projectLoader = new ProjectLoaderStub(PROJECT1); 
    clock.setTime(TIME1);
    jobStoreCenter.dealWithProject(PROJECT1, projectLoader);
    
    int expectedNumberOfObjects = 1;
    int actualNumberOfObjects = datastore.prepare(new Query()).countEntities(withLimit(10));
    Assert.assertEquals(expectedNumberOfObjects, actualNumberOfObjects);
    
    Key projectKey = KeyFactory.createKey("Project", PROJECT1);
    Entity actualProject = datastore.get(projectKey);

    Assert.assertEquals(TIME1, actualProject.getProperty("lastAccessed"));
  }

  public void addTwiceTheSameProject() throws EntityNotFoundException, IOException {
    // Add the same project twice. The Datastore should contain only one project
    ProjectLoader projectLoader = new ProjectLoaderStub(PROJECT1); 
    clock.setTime(TIME1);
    jobStoreCenter.dealWithProject(PROJECT1, projectLoader);

    clock.setTime(TIME2);
    jobStoreCenter.dealWithProject(PROJECT1, projectLoader);
    
    int expectedNumberOfObjects = 1;
    int actualNumberOfObjects = datastore.prepare(new Query()).countEntities(withLimit(10));
    Assert.assertEquals(expectedNumberOfObjects, actualNumberOfObjects);
    
    Key projectKey = KeyFactory.createKey("Project", PROJECT1);
    Entity actualProject = datastore.get(projectKey);

    Assert.assertEquals(TIME2, actualProject.getProperty("lastAccessed"));
  }

  @Test
  public void addProjectWithRunningJob() throws IOException, EntityNotFoundException {
    // Add a project that has only one job (the job is running)
    Map<String, List<JobJSON>> mapOfJobs = new HashMap<>();
    mapOfJobs.put("europe-west1", Arrays.asList(JOB1));

    ProjectLoader projectLoader = new ProjectLoaderStub(mapOfJobs, PROJECT1);
    clock.setTime(TIME2);
    jobStoreCenter.dealWithProject(PROJECT1, projectLoader);
    
    // Datastore should contain only 2 elements
    int expectedNumberOfObjects = 2;
    int actualNumberOfObjects = datastore.prepare(new Query()).countEntities();
    Assert.assertEquals(expectedNumberOfObjects, actualNumberOfObjects);

    int expectedNumberOfProjects = 1;
    int actualNumberOfProjects = datastore.prepare(new Query("Project")).countEntities();
    Assert.assertEquals(expectedNumberOfProjects, actualNumberOfProjects);

    int expectedNumberOfJobs = 1;
    int actualNumberOfJobs = datastore.prepare(new Query("RunningJob")).countEntities();
    Assert.assertEquals(expectedNumberOfJobs, actualNumberOfJobs);
  }

  @Test
  public void addProjectWithFinalisedJob() throws IOException, EntityNotFoundException {
    // Add a project that has only one job (the job is finalised)
    Map<String, List<JobJSON>> mapOfJobs = new HashMap<>();
    mapOfJobs.put("europe-west2", Arrays.asList(JOB2));

    ProjectLoader projectLoader = new ProjectLoaderStub(mapOfJobs, PROJECT1);
    clock.setTime(TIME2);
    jobStoreCenter.dealWithProject(PROJECT1, projectLoader);
    
    // Datastore should contain only 2 elements
    int expectedNumberOfObjects = 2;
    int actualNumberOfObjects = datastore.prepare(new Query()).countEntities();
    Assert.assertEquals(expectedNumberOfObjects, actualNumberOfObjects);

    int expectedNumberOfProjects = 1;
    int actualNumberOfProjects = datastore.prepare(new Query("Project")).countEntities();
    Assert.assertEquals(expectedNumberOfProjects, actualNumberOfProjects);

    int expectedNumberOfJobs = 1;
    int actualNumberOfJobs = datastore.prepare(new Query("FinalisedJob")).countEntities();
    Assert.assertEquals(expectedNumberOfJobs, actualNumberOfJobs);
  }

  @Test
  public void addJobWithBothTypesOfJobs() throws IOException, EntityNotFoundException {
    // Add a project that has only one job (the job is finalised)
    Map<String, List<JobJSON>> mapOfJobs = new HashMap<>();
    mapOfJobs.put("europe-west1", Arrays.asList(JOB1));
    mapOfJobs.put("europe-west2", Arrays.asList(JOB2));

    ProjectLoader projectLoader = new ProjectLoaderStub(mapOfJobs, PROJECT1);
    clock.setTime(TIME2);
    jobStoreCenter.dealWithProject(PROJECT1, projectLoader);
    
    int expectedNumberOfObjects = 3;
    int actualNumberOfObjects = datastore.prepare(new Query()).countEntities();
    Assert.assertEquals(expectedNumberOfObjects, actualNumberOfObjects);

    int expectedNumberOfProjects = 1;
    int actualNumberOfProjects = datastore.prepare(new Query("Project")).countEntities();
    Assert.assertEquals(expectedNumberOfProjects, actualNumberOfProjects);

    int expectedNumberOfJobs = 1;
    int actualNumberOfJobs = datastore.prepare(new Query("FinalisedJob")).countEntities();
    Assert.assertEquals(expectedNumberOfJobs, actualNumberOfJobs);

    int expectedNumberOfRunningJobs = 1;
    int actualNumberOfRunningJobs = datastore.prepare(new Query("RunningJob")).countEntities();
    Assert.assertEquals(expectedNumberOfRunningJobs, actualNumberOfRunningJobs);
  }

  @Test
  public void updateProject() throws IOException, EntityNotFoundException {
    // Add a project that has a job. Than update that job. The job is still running, after the update
    Map<String, List<JobJSON>> mapOfJobs = new HashMap<>();
    mapOfJobs.put("europe-west1", Arrays.asList(JOB1));

    ProjectLoaderStub projectLoader = new ProjectLoaderStub(mapOfJobs, PROJECT1);
    clock.setTime(TIME2);
    jobStoreCenter.dealWithProject(PROJECT1, projectLoader);
    
    // Update the project in the server
    Map<JobJSON, Boolean> updateHelpersJobs = new HashMap<>();
    updateHelpersJobs.put(JOB1_UPDATED_RUNNING, true);
    projectLoader.setUpdate(updateHelpersJobs, Arrays.asList(JOB1_RUNNING_AFTER_UPDATE));

    clock.setTime(TIME4);
    jobStoreCenter.dealWithProject(PROJECT1, projectLoader);
    
    int expectedNumberOfObjects = 2;
    int actualNumberOfObjects = datastore.prepare(new Query()).countEntities();
    Assert.assertEquals(expectedNumberOfObjects, actualNumberOfObjects);

    int expectedNumberOfProjects = 1;
    int actualNumberOfProjects = datastore.prepare(new Query("Project")).countEntities();
    Assert.assertEquals(expectedNumberOfProjects, actualNumberOfProjects);

    int expectedNumberOfJobs = 1;
    int actualNumberOfJobs = datastore.prepare(new Query("RunningJob")).countEntities();
    Assert.assertEquals(expectedNumberOfJobs, actualNumberOfJobs);

    List<JobJSON> expectedList =  Arrays.asList(JOB1_RUNNING_AFTER_UPDATE);
    List<JobJSON> actualList = jobStoreCenter.getJobsFromDatastore(PROJECT1);
    Assert.assertEquals(expectedList, actualList);
  }

  @Test
  public void updatedJobFromRunningToFinalised() throws IOException{
    // Add a project that has a job. Than update that job. The job is finalised, so 
    // it should be updated from RunningJob to FinalisedJob 
    Map<String, List<JobJSON>> mapOfJobs = new HashMap<>();
    mapOfJobs.put("europe-west1", Arrays.asList(JOB1));

    ProjectLoaderStub projectLoader = new ProjectLoaderStub(mapOfJobs, PROJECT1);
    clock.setTime(TIME2);
    jobStoreCenter.dealWithProject(PROJECT1, projectLoader);
    
    // Make the update at the server
    Map<JobJSON, Boolean> updateHelpersJobs = new HashMap<>();
    updateHelpersJobs.put(JOB1_UPDATED_FINALISED, true);
    projectLoader.setUpdate(updateHelpersJobs, Arrays.asList(JOB1_FINALISED_AFTER_UPDATE));

    clock.setTime(TIME4);
    jobStoreCenter.dealWithProject(PROJECT1, projectLoader);
    
    int expectedNumberOfObjects = 2;
    int actualNumberOfObjects = datastore.prepare(new Query()).countEntities();
    Assert.assertEquals(expectedNumberOfObjects, actualNumberOfObjects);

    int expectedNumberOfProjects = 1;
    int actualNumberOfProjects = datastore.prepare(new Query("Project")).countEntities();
    Assert.assertEquals(expectedNumberOfProjects, actualNumberOfProjects);

    int expectedNumberOfJobs = 1;
    int actualNumberOfJobs = datastore.prepare(new Query("FinalisedJob")).countEntities();
    Assert.assertEquals(expectedNumberOfJobs, actualNumberOfJobs);

    List<JobJSON> expectedList =  Arrays.asList(JOB1_FINALISED_AFTER_UPDATE);
    List<JobJSON> actualList = jobStoreCenter.getJobsFromDatastore(PROJECT1);
    Assert.assertEquals(expectedList, actualList); 
  }

  @Test
  public void updateProjectWith2Jobs() throws IOException {
    // Add a project that has both a running job and a finalised one. 
    // Update the running job. The finalised one should not be modified

    Map<String, List<JobJSON>> mapOfJobs = new HashMap<>();
    mapOfJobs.put("europe-west1", Arrays.asList(JOB1));
    mapOfJobs.put("europe-west2", Arrays.asList(JOB2));

    ProjectLoaderStub projectLoader = new ProjectLoaderStub(mapOfJobs, PROJECT1);
    clock.setTime(TIME2);
    jobStoreCenter.dealWithProject(PROJECT1, projectLoader);
    
    // Update the project in the server
    Map<JobJSON, Boolean> updateHelpersJobs = new HashMap<>();
    updateHelpersJobs.put(JOB1_UPDATED_RUNNING, true);
    updateHelpersJobs.put(JOB2_NO_UPDATE, false);
    projectLoader.setUpdate(updateHelpersJobs, Arrays.asList(JOB1_RUNNING_AFTER_UPDATE));

    clock.setTime(TIME4);
    jobStoreCenter.dealWithProject(PROJECT1, projectLoader);
    
    int expectedNumberOfObjects = 3;
    int actualNumberOfObjects = datastore.prepare(new Query()).countEntities();
    Assert.assertEquals(expectedNumberOfObjects, actualNumberOfObjects);

    int expectedNumberOfProjects = 1;
    int actualNumberOfProjects = datastore.prepare(new Query("Project")).countEntities();
    Assert.assertEquals(expectedNumberOfProjects, actualNumberOfProjects);

    int expectedNumberOfRunningJobs = 1;
    int actualNumberOfRunningJobs = datastore.prepare(new Query("RunningJob")).countEntities();
    Assert.assertEquals(expectedNumberOfRunningJobs, actualNumberOfRunningJobs);

    int expectedNumberOfFinalisedJobs = 1;
    int actualNumberOfFinalisedJobs = datastore.prepare(new Query("FinalisedJob")).countEntities();
    Assert.assertEquals(expectedNumberOfFinalisedJobs, actualNumberOfFinalisedJobs);

    List<JobJSON> expectedList =  Arrays.asList(JOB2, JOB1_RUNNING_AFTER_UPDATE);
    List<JobJSON> actualList = jobStoreCenter.getJobsFromDatastore(PROJECT1);
    Assert.assertEquals(expectedList, actualList);
  }

  @Test
  public void updateProject2FinalisedJobs() throws IOException {
    // Tha jobs has 1 RunningJob and one finalised one. The running job is 
    // terminated, so just the running one should be updated into a FinalisedJob

    Map<String, List<JobJSON>> mapOfJobs = new HashMap<>();
    mapOfJobs.put("europe-west1", Arrays.asList(JOB1));
    mapOfJobs.put("europe-west2", Arrays.asList(JOB2));

    ProjectLoaderStub projectLoader = new ProjectLoaderStub(mapOfJobs, PROJECT1);
    clock.setTime(TIME2);
    jobStoreCenter.dealWithProject(PROJECT1, projectLoader);
    
    // Update the project in the server
    Map<JobJSON, Boolean> updateHelpersJobs = new HashMap<>();
    updateHelpersJobs.put(JOB1_UPDATED_FINALISED, true);
    updateHelpersJobs.put(JOB2_NO_UPDATE, false);
    projectLoader.setUpdate(updateHelpersJobs, Arrays.asList(JOB1_FINALISED_AFTER_UPDATE));

    clock.setTime(TIME4);
    jobStoreCenter.dealWithProject(PROJECT1, projectLoader);
    
    int expectedNumberOfObjects = 3;
    int actualNumberOfObjects = datastore.prepare(new Query()).countEntities();
    Assert.assertEquals(expectedNumberOfObjects, actualNumberOfObjects);

    int expectedNumberOfProjects = 1;
    int actualNumberOfProjects = datastore.prepare(new Query("Project")).countEntities();
    Assert.assertEquals(expectedNumberOfProjects, actualNumberOfProjects);

    int expectedNumberOfRunningJobs = 2;
    int actualNumberOfRunningJobs = datastore.prepare(new Query("FinalisedJob")).countEntities();
    Assert.assertEquals(expectedNumberOfRunningJobs, actualNumberOfRunningJobs);

    List<JobJSON> expectedList =  Arrays.asList(JOB1_FINALISED_AFTER_UPDATE, JOB2);
    List<JobJSON> actualList = jobStoreCenter.getJobsFromDatastore(PROJECT1);
    Assert.assertEquals(expectedList, actualList);
  }

  @Test
  public void getJobsFromEmtpyProject() throws IOException {
    ProjectLoader projectLoader = new ProjectLoaderStub(PROJECT1); 
    clock.setTime(TIME1);
    jobStoreCenter.dealWithProject(PROJECT1, projectLoader);

    List<JobJSON> expectedList = new ArrayList<>();
    List<JobJSON> actualList = jobStoreCenter.getJobsFromDatastore(PROJECT1);

    Assert.assertEquals(expectedList, actualList);
  }

  @Test
  public void getJobsFromProject() throws IOException {
    Map<String, List<JobJSON>> mapOfJobs = new HashMap<>();
    mapOfJobs.put("europe-west1", Arrays.asList(JOB1, JOB4));
    mapOfJobs.put("europe-west2", Arrays.asList(JOB2, JOB3));

    ProjectLoaderStub projectLoader = new ProjectLoaderStub(mapOfJobs, PROJECT1);
    clock.setTime(TIME2);
    jobStoreCenter.dealWithProject(PROJECT1, projectLoader);

    List<JobJSON> expectedList = Arrays.asList(JOB2, JOB4, JOB1, JOB3);
    List<JobJSON> actualList = jobStoreCenter.getJobsFromDatastore(PROJECT1);

    Assert.assertEquals(expectedList, actualList);
  }

  @Test
  public void multipleProjects() throws IOException {
    // Add the first project
    Map<String, List<JobJSON>> mapOfJobs1 = new HashMap<>();
    mapOfJobs1.put("europe-west1", Arrays.asList(JOB1, JOB4));
    mapOfJobs1.put("europe-west2", Arrays.asList(JOB2, JOB3));
    ProjectLoaderStub projectLoader1 = new ProjectLoaderStub(mapOfJobs1, PROJECT1);

    clock.setTime(TIME1);
    jobStoreCenter.dealWithProject(PROJECT1, projectLoader1); 

    // Add the second project
    Map<String, List<JobJSON>>mapOfJobs2 = new HashMap<>();
    mapOfJobs2.put("europe-west1", Arrays.asList(JOB5, JOB6));
    ProjectLoaderStub projectLoader2 = new ProjectLoaderStub(mapOfJobs2, PROJECT2);

    clock.setTime(TIME2);
    jobStoreCenter.dealWithProject(PROJECT2, projectLoader2);

    List<JobJSON> expectedList = Arrays.asList(JOB5, JOB6);
    List<JobJSON> actualList = jobStoreCenter.getJobsFromDatastore(PROJECT2);

    Assert.assertEquals(expectedList, actualList);
  }
  
}
    