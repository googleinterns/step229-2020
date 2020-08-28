// Copyright 2020 Google LLC

/**
 * @author andreeanica
 */

package com.google.sps;

import org.junit.Ignore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.io.IOException;
import com.google.sps.data.JobJSON;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

@RunWith(JUnit4.class)
public final class AggregationCenterTest {
    private AggregationCenter aggregationCenter;

    private static final JobJSON JOB1 = new JobJSON(null, "beamsqldemopipeline-ihr-0804214611-d6c2f941", null,
                                                        null, "2.23.0", "SUPPORTED", "europe-west1", 0, null, null, null, null, 
                                                            null, null, null, null, null, null, null, null, "Apache Beam SDK for Java");
    private static final JobJSON JOB2 = new JobJSON(null, "beamsqldemopipeline-ihr-0804213246-8dbbe1e9", null,
                                                        null, "2.23.0-SNAPSHOT", "STALE", "us-central1", 0, null, null, null, null, 
                                                            null, null, null, null, null, null, null, null, null);
    private static final JobJSON JOB3 = new JobJSON(null, "otherName-otherUser-0804214611-d6c2f941", null,
                                                        null, "2.23.0", "SUPPORTED", "europe-west1", 0, null, null, null, null, 
                                                            null, null, null, null, null, null, null, null, null);
    private static final JobJSON JOB4 = new JobJSON(null, "jobDoesn'tRespectNaming", null,
                                                        null, "2.23.0", "SUPPORTED", "europe-west2", 0, null, null, null, null, 
                                                            null, null, null, null, null, null, null, null, null);
    

    @Before
    public void setUp() {
      aggregationCenter = new AggregationCenter();
    }

    @Test
    public void oneJobOneName()  {
      List<JobJSON> jobs = Arrays.asList(JOB1);

      Map<String, List<JobJSON>> expectedMap = new HashMap<>();
      expectedMap.put("beamsqldemopipeline", jobs);

      Map<String, List<JobJSON>> actualMap = aggregationCenter.aggregateByName(jobs);
     
      Assert.assertEquals(expectedMap, actualMap);
    }

    @Test
    public void multipleJobsMultipleNames() {
      List<JobJSON> jobs = Arrays.asList(JOB1, JOB2, JOB3);

      Map<String, List<JobJSON>> expectedMap = new HashMap<>();
      expectedMap.put("beamsqldemopipeline", Arrays.asList(JOB1, JOB2));
      expectedMap.put("otherName", Arrays.asList(JOB3));

      Map<String, List<JobJSON>> actualMap = aggregationCenter.aggregateByName(jobs);
     
      Assert.assertEquals(expectedMap, actualMap);
    }

    @Test
    public void jobDoesntRespectNaming() {
      List<JobJSON> jobs = Arrays.asList(JOB4);
      
      Map<String, List<JobJSON>> actualMap = aggregationCenter.aggregateByName(jobs);
     
      Assert.assertTrue(actualMap.isEmpty());
    }

    @Test
    public void mixedNamedJobs() {
      List<JobJSON> jobs = Arrays.asList(JOB1, JOB2, JOB3, JOB4);

      Map<String, List<JobJSON>> expectedMap = new HashMap<>();
      expectedMap.put("beamsqldemopipeline", Arrays.asList(JOB1, JOB2));
      expectedMap.put("otherName", Arrays.asList(JOB3));

      Map<String, List<JobJSON>> actualMap = aggregationCenter.aggregateByName(jobs);
     
      Assert.assertEquals(expectedMap, actualMap);
    }

    @Test
    public void noJobs() {
      List<JobJSON> jobs = Arrays.asList();

      Map<String, List<JobJSON>> actualMap = aggregationCenter.aggregateByName(jobs);
     
      Assert.assertTrue(actualMap.isEmpty());
    }

    @Test
    public void oneJobOneUser() {
      List<JobJSON> jobs = Arrays.asList(JOB1);

      Map<String, List<JobJSON>> expectedMap = new HashMap<>();
      expectedMap.put("ihr", jobs);

      Map<String, List<JobJSON>> actualMap = aggregationCenter.aggregateByUser(jobs);
     
      Assert.assertEquals(expectedMap, actualMap);
    }

    @Test
    public void multipleUsers() {
      List<JobJSON> jobs = Arrays.asList(JOB1, JOB2, JOB3);

      Map<String, List<JobJSON>> expectedMap = new HashMap<>();
      expectedMap.put("ihr", Arrays.asList(JOB1, JOB2));
      expectedMap.put("otherUser", Arrays.asList(JOB3));

      Map<String, List<JobJSON>> actualMap = aggregationCenter.aggregateByUser(jobs);
     
      Assert.assertEquals(expectedMap, actualMap);
    }

    @Test
    public void noUser() {
      List<JobJSON> jobs = Arrays.asList(JOB4);

      Map<String, List<JobJSON>> actualMap = aggregationCenter.aggregateByUser(jobs);
     
      Assert.assertTrue(actualMap.isEmpty());
    }

    @Test
    public void multipleUserMultipleNames() {
      List<JobJSON> jobs = Arrays.asList(JOB1, JOB2, JOB3, JOB4);

      Map<String, List<JobJSON>> expectedMap = new HashMap<>();
      expectedMap.put("ihr", Arrays.asList(JOB1, JOB2));
      expectedMap.put("otherUser", Arrays.asList(JOB3));

      Map<String, List<JobJSON>> actualMap = aggregationCenter.aggregateByUser(jobs);
     
      Assert.assertEquals(expectedMap, actualMap);
    }

    @Test
    public void oneJobOneRegion() {
      List<JobJSON> jobs = Arrays.asList(JOB1);

      Map<String, List<JobJSON>> expectedMap = new HashMap<>();
      expectedMap.put("europe-west1", jobs);

      Map<String, List<JobJSON>> actualMap = aggregationCenter.aggregateByRegion(jobs);
     
      Assert.assertEquals(expectedMap, actualMap);
    }

    @Test
    public void multipleRegions() {
      List<JobJSON> jobs = Arrays.asList(JOB1, JOB2, JOB3, JOB4);

      Map<String, List<JobJSON>> expectedMap = new HashMap<>();
      expectedMap.put("europe-west1", Arrays.asList(JOB1, JOB3));
      expectedMap.put("us-central1", Arrays.asList(JOB2));
      expectedMap.put("europe-west2", Arrays.asList(JOB4));

      Map<String, List<JobJSON>> actualMap = aggregationCenter.aggregateByRegion(jobs);
     
      Assert.assertEquals(expectedMap, actualMap);
    }

    @Test 
    public void NoJobsNoRegions() {
      List<JobJSON> jobs = Arrays.asList();

      Map<String, List<JobJSON>> actualMap = aggregationCenter.aggregateByRegion(jobs);
     
      Assert.assertTrue(actualMap.isEmpty());
    }

    @Test 
    public void oneJobOneSDK() {
      List<JobJSON> jobs = Arrays.asList(JOB1);

      Map<String, List<JobJSON>> expectedMap = new HashMap<>();
      expectedMap.put("2.23.0 SUPPORTED", jobs);

      Map<String, List<JobJSON>> actualMap = aggregationCenter.aggregateBySDK(jobs);
     
      Assert.assertEquals(expectedMap, actualMap);
    }

    @Test 
    public void multipleJobsMultipleSDK() {
      List<JobJSON> jobs = Arrays.asList(JOB1, JOB2, JOB3, JOB4);

      Map<String, List<JobJSON>> expectedMap = new HashMap<>();
      expectedMap.put("2.23.0 SUPPORTED", Arrays.asList(JOB1, JOB3, JOB4));
      expectedMap.put("2.23.0-SNAPSHOT STALE", Arrays.asList(JOB2));

      Map<String, List<JobJSON>> actualMap = aggregationCenter.aggregateBySDK(jobs);
     
      Assert.assertEquals(expectedMap, actualMap);
    }

    @Test 
    public void oneJobOneSDKSupportStatus() {
      List<JobJSON> jobs = Arrays.asList(JOB1);

      Map<String, List<JobJSON>> expectedMap = new HashMap<>();
      expectedMap.put("SUPPORTED", jobs);

      Map<String, List<JobJSON>> actualMap = aggregationCenter.aggregateBySDKSupportStatus(jobs);
     
      Assert.assertEquals(expectedMap, actualMap);
    }

    @Test 
    public void multipleJobsMultipleSDKSupportStatus() {
      List<JobJSON> jobs = Arrays.asList(JOB1, JOB2, JOB3, JOB4);

      Map<String, List<JobJSON>> expectedMap = new HashMap<>();
      expectedMap.put("SUPPORTED", Arrays.asList(JOB1, JOB3, JOB4));
      expectedMap.put("STALE", Arrays.asList(JOB2));

      Map<String, List<JobJSON>> actualMap = aggregationCenter.aggregateBySDKSupportStatus(jobs);
     
      Assert.assertEquals(expectedMap, actualMap);
    }

    @Test
    public void oneJobOneProgrammingLanguage() {
      List<JobJSON> jobs = Arrays.asList(JOB1);

      Map<String, List<JobJSON>> expectedMap = new HashMap<>();
      expectedMap.put("Java", jobs);

      Map<String, List<JobJSON>> actualMap = aggregationCenter.aggregateByProgrammingLanguage(jobs);
     
      Assert.assertEquals(expectedMap, actualMap);
    }


}