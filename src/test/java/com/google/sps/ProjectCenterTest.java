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
import java.security.GeneralSecurityException;
import com.google.sps.data.JobModel;

@RunWith(JUnit4.class)
public final class ProjectCenterTest {
    private ProjectCenter projectCenter;

    @Before
    public void setUp() throws IOException , GeneralSecurityException {
      projectCenter = new ProjectCenter("bt-dataflow-sql-demo", "bt-dataflow-sql-demo.json");
    }

    @Test
    public void fetchJobsTest() throws IOException {
      int actualNumberJobs = projectCenter.fetchJobs().size();
      int expectedNumberJobs = 17;
      Assert.assertEquals(actualNumberJobs, expectedNumberJobs);
    }

    @Test
    public void fetchTest() throws IOException {
        String actualName = projectCenter.fetch("2020-08-06_06_01_04-4314082517434006770").name;
        String expectedName = "dfsql-with-tumble";
        Assert.assertEquals(actualName, expectedName);
    }

    @Test
    public void fetchKnownLocationTest() throws IOException {
        String actualName = projectCenter.fetch("2020-08-06_06_01_04-4314082517434006770", "europe-west2").name;
        String expectedName = "dfsql-with-tumble";
        Assert.assertEquals(actualName, expectedName);
    }

    @Test 
    public void fetchJobNotFoundTest() throws IOException {
        JobModel actualJob = projectCenter.fetch("dumb-id");
        JobModel expectedJob = null;
        Assert.assertEquals(actualJob, expectedJob);
    }

    @Test 
    public void fetchJobNotFoundKnownLocationTest() throws IOException {
        JobModel actualJob = projectCenter.fetch("dumb-id", "asia-east1");
        JobModel expectedJob = null;
        Assert.assertEquals(actualJob, expectedJob);
    }

     @Test 
    public void fetchJobNotFoundWrongLocationTest() throws IOException {
        JobModel actualJob = projectCenter.fetch("2020-08-06_06_01_04-4314082517434006770", "asia-east1");
        JobModel expectedJob = null;
        Assert.assertEquals(actualJob, expectedJob);
    }   
}