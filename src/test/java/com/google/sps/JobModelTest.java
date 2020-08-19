// Copyright 2020 Google LLC

/**
 * @author andreeanica
 */

package com.google.sps;

import java.sql.Timestamp;
import org.junit.Assume;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import java.io.IOException;
import java.security.GeneralSecurityException;
import com.google.sps.data.JobModel;

@RunWith(Parameterized.class)
public final class JobModelTest {

    @Parameters(name = "JobId : {0}")
    public static Iterable<Object []> data() 
    {
        return Arrays.asList(new Object[][] { 
            { "2020-08-06_06_01_04-4314082517434006770", "bt-dataflow-sql-demo", "dfsql-with-tumble", "JOB_TYPE_STREAMING",
              "JOB_STATE_CANCELLED", "STALE", "2.23.0-SNAPSHOT", "europe-west2", 0 , "2020-08-06T13:01:05.239867Z",
                5.55, 20.813, 83.251, 0.0, 2, true, 15.53 }, 
            { "2020-08-06_05_48_10-15244611514655055844", "bt-dataflow-sql-demo", "dfsql-bt-easy-to-identify", "JOB_TYPE_STREAMING",
              "JOB_STATE_CANCELLED", "STALE", "2.23.0-SNAPSHOT", "europe-west2", 0, "2020-08-06T12:48:11.885373Z",
                5.98, 22.424, 89.696, 0.0, 2, true, 14.97 }, 
            { "2020-08-06_05_28_24-6040859398619218325", "bt-dataflow-sql-demo", "beamsqldemopipeline-ihr-0806122805-aa08f704", "JOB_TYPE_STREAMING",
              "JOB_STATE_CANCELLED", "SUPPORTED", "2.23.0", "europe-west1", 0 , "2020-08-06T12:28:26.499518Z",
                6.688, 25.082, 100.327, 0.0, 2, true, 30638.08 }, 
            { "2020-08-06_03_53_30-15110274477063034935", "bt-dataflow-sql-demo", "dfsql-65f4a130-173c3645af2", "JOB_TYPE_STREAMING",
              "JOB_STATE_CANCELLED", "STALE", "2.23.0-SNAPSHOT", "us-central1", 0, "2020-08-06T10:53:31.543230Z",
                3.779, 14.17, 56.68, 0.0, 2, true, 9.06 }, 
            { "2020-08-06_03_50_01-9654167820707455539", "bt-dataflow-sql-demo", "dfsql-43d5b99-173c357c23e", "JOB_TYPE_STREAMING",
              "JOB_STATE_CANCELLED", "STALE", "2.23.0-SNAPSHOT", "us-central1", 0, "2020-08-06T10:50:02.089393Z",
                3.876, 14.537, 58.146, 0.0, 2, true, 8.47 }, 
            { "2020-08-06_00_47_39-7199932943153518565", "bt-dataflow-sql-demo", "beamsqldemopipeline-ihr-0806074705-d05f6ed7", "JOB_TYPE_STREAMING",
              "JOB_STATE_CANCELLED", "SUPPORTED", "2.23.0", "europe-west1", 0, "2020-08-06T07:47:41.890279Z",
                26.666, 99.998, 399.992, 0.0, 6, true, 94699.52 }, 
            { "2020-08-05_02_07_44-11496906389150035870", "bt-dataflow-sql-demo", "dfsql-join-and-window", "JOB_TYPE_STREAMING", 
              "JOB_STATE_CANCELLED", "STALE", "2.23.0-SNAPSHOT", "us-central1", 0, "2020-08-05T09:07:45.269405Z",
                0.656, 2.461, 9.844, 0.0, 2, true, 0.0 }, 
            { "2020-08-05_01_27_19-12034076432132404549", "bt-dataflow-sql-demo", "dfsql-query-example-topic-table", "JOB_TYPE_STREAMING", 
              "JOB_STATE_CANCELLED", "STALE", "2.23.0-SNAPSHOT", "us-central1", 0, "2020-08-05T08:27:20.292802Z",
                1.197, 4.489, 17.958, 0.0, 2, true, 3.34 }, 
            { "2020-08-04_14_46_51-10622717568042133093", "bt-dataflow-sql-demo", "beamsqldemopipeline-ihr-0804214611-d6c2f941", "JOB_TYPE_STREAMING",
              "JOB_STATE_CANCELLED", "SUPPORTED", "2.23.0", "europe-west1", 0, "2020-08-04T21:46:53.637572Z",
                547.03, 2051.36, 8205.456, 0.0, 6, true, 390584.32 }, 
            { "2020-08-04_14_33_26-5454611298510143581", "bt-dataflow-sql-demo", "beamsqldemopipeline-ihr-0804213246-8dbbe1e9", "JOB_TYPE_STREAMING", 
              "JOB_STATE_CANCELLED", "SUPPORTED", "2.23.0", "europe-west1", 0, "2020-08-04T21:33:28.350686Z",
                0.326, 1.222, 4.889, 0.0, 2, true, 61.91  }, 
            { "2020-08-04_13_09_00-4586883393904972800", "bt-dataflow-sql-demo", "beamsqldemopipeline-ihr-0804200823-809e448a", "JOB_TYPE_STREAMING", 
              "JOB_STATE_FAILED", "SUPPORTED", "2.23.0", "europe-west1", 0, "2020-08-04T20:09:02.212104Z",
                null, null, null, null, null, false, null } }); // Failed
    }

    private ProjectCenter projectCenter;

    private JobModel jobModel;

    private final String jobId;
    private final String projectId;
    private final String name;
    private final String type;
    private final String status;
    private final String sdkStatus;
    private final String sdk;
    private final String region;
    private final int currentWorkers;
    private final String startTime;
    private final Double totalVCPUTime; // in hours
    private final Double totalMemoryTime; // in Gb hours
    private final Double totalDiskTimeHDD; // in GB hours
    private final Double totalDiskTimeSSD; // in GB hours
    private final Integer currentVcpuCount;
    private final Double totalStreamingData; // in MB
    private final Boolean enableStreamingEngine;

    public JobModelTest(final String jobId, final String projectId, final String name, final String type,
                            final String status, final String sdkStatus, final String sdk, 
                                final String region, final int currentWorkers, final String startTime,
                                    final Double totalVCPUTime, final Double totalMemoryTime, final Double totalDiskTimeHDD,
                                        final Double totalDiskTimeSSD, final Integer currentVcpuCount,
                                            final Boolean enableStreamingEngine, final Double totalStreamingData)
                                                throws IOException, GeneralSecurityException {
      this.jobId = jobId;
      this.projectId = projectId;
      this.name = name;
      this.type = type;
      this.status = status;
      this.sdkStatus = sdkStatus;
      this.sdk = sdk;
      this.region = region;
      this.currentWorkers = currentWorkers;
      this.startTime = startTime;
      this.totalVCPUTime = totalVCPUTime;
      this.totalMemoryTime = totalMemoryTime;
      this.totalDiskTimeHDD = totalDiskTimeHDD;
      this.totalDiskTimeSSD = totalDiskTimeSSD;
      this.currentVcpuCount = currentVcpuCount;
      this.enableStreamingEngine = enableStreamingEngine;
      this.totalStreamingData = totalStreamingData;

      projectCenter = new ProjectCenter("bt-dataflow-sql-demo", "bt-dataflow-sql-demo.json");
      jobModel = projectCenter.fetch(jobId, region);
    }

    @Test
    public void TestProjectIdforJob() {
        Assert.assertEquals(projectId, jobModel.projectId);
    }

    @Test
    public void TestNameforJob() {
        Assert.assertEquals(name, jobModel.name);
    }

    @Test
    public void TestTypeforJob() {
        Assert.assertEquals(type, jobModel.type);
    }

    @Test
    public void TestSDKStatusforJob() {
        Assert.assertEquals(sdkStatus, jobModel.sdkSupportStatus);
    }

    @Test
    public void TestSDKforJob() {
        Assert.assertEquals(sdk, jobModel.sdk);
    }

    // TODO: - add test with currentWorkers != 0;
    @Test
    public void TestCurrentWorkers() {
        Assert.assertEquals(currentWorkers, jobModel.currentWorkers);
    }

    @Test
    public void TestStartTime() {
        Assert.assertEquals(startTime, jobModel.startTime);
    }

    @Test
    public void TestTotalVCPUTimeInHours() {
        Assume.assumeTrue(jobModel.state.compareTo("JOB_STATE_FAILED") != 0);
        Double actualVCPUTimeinHours = (jobModel.totalVCPUTime / 3600);
        Assert.assertEquals(totalVCPUTime, actualVCPUTimeinHours, 0.001);
    }

    public void TestTotalVCPUTimeInHoursFailedJob() {
        Assume.assumeTrue(jobModel.state.compareTo("JOB_STATE_FAILED") == 0);
        Assert.assertEquals(null, jobModel.totalVCPUTime);
    }

    @Test
    public void TestTotalMemoryTimeInGBHours() {
        Assume.assumeTrue(jobModel.state.compareTo("JOB_STATE_FAILED") != 0);
        Double actualTotalMemoryTimeinGBHours = (jobModel.totalMemoryTime / (3600 * 1024));
        Assert.assertEquals(totalMemoryTime, actualTotalMemoryTimeinGBHours, 0.005);
    }

    @Test
    public void TestTotalMemoryTimeInGBHoursFailedJob() {
        Assume.assumeTrue(jobModel.state.compareTo("JOB_STATE_FAILED") == 0);
        Assert.assertEquals(null, jobModel.totalMemoryTime);
    }

    @Test
    public void TestTotalDiskTimeHDD() {
        Assume.assumeTrue(jobModel.state.compareTo("JOB_STATE_FAILED") != 0);
        Double actualTotalDiskTimeHDD = jobModel.totalDiskTimeHDD / 3600;
        Assert.assertEquals(totalDiskTimeHDD, actualTotalDiskTimeHDD, 0.001);
    }

    @Test
    public void TestTotalDiskTimeHDDFailedJob() {
        Assume.assumeTrue(jobModel.state.compareTo("JOB_STATE_FAILED") == 0);
        Assert.assertEquals(null, jobModel.totalDiskTimeHDD);
    }

    @Test
    public void TestTotalDiskTimeSSD() {
        Assume.assumeTrue(jobModel.state.compareTo("JOB_STATE_FAILED") != 0);
        Double actualTotalDiskTimeSSD = jobModel.totalDiskTimeSSD / 3600;
        Assert.assertEquals(totalDiskTimeSSD, actualTotalDiskTimeSSD, 0.001);
    }

    @Test
    public void TestTotalDiskTimeSSDFailedJob() {
        Assume.assumeTrue(jobModel.state.compareTo("JOB_STATE_FAILED") == 0);
        Assert.assertEquals(null, jobModel.totalDiskTimeSSD);
    }

    @Test
    public void TestCurrentVcpuCount() {
        Assume.assumeTrue(jobModel.state.compareTo("JOB_STATE_FAILED") != 0);
        Assert.assertEquals(currentVcpuCount, jobModel.currentVcpuCount);
    }

    @Test
    public void TestCurrentVcpuCountFailedJob() {
        Assume.assumeTrue(jobModel.state.compareTo("JOB_STATE_FAILED") == 0);
        Assert.assertEquals(null, jobModel.currentVcpuCount);
    }

    @Test
    public void TestEnableStreamingEngine() {
        Assert.assertEquals(enableStreamingEngine, jobModel.enableStreamingEngine);
    }

    @Test
    public void TestTotalStreamingData() {
        Assume.assumeTrue(jobModel.enableStreamingEngine);
        Double actualTotalStreamingData = jobModel.totalStreamingData * 1024;
        Assert.assertEquals(totalStreamingData, actualTotalStreamingData, 5);
    }

    @Test
    public void TestTotalStreamingNotEnabled() {
        Assume.assumeTrue(!jobModel.enableStreamingEngine);
        Assert.assertEquals(null, jobModel.totalStreamingData);
    }

    
}