// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.sql.Timestamp;
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

@RunWith(Parameterized.class)
public final class JobModelTest {

    @Parameters(name = "JobId : {0}")
    public static Iterable<Object []> data() 
    {
        return Arrays.asList(new Object[][] { 
            { "2020-08-06_06_01_04-4314082517434006770", "bt-dataflow-sql-demo", "dfsql-with-tumble", "JOB_TYPE_STREAMING",
              "JOB_STATE_CANCELLED", "STALE", "2.23.0-SNAPSHOT", "europe-west2", 0 , "2020-08-06T13:01:05.239867Z"}, 
            { "2020-08-06_05_48_10-15244611514655055844", "bt-dataflow-sql-demo", "dfsql-bt-easy-to-identify", "JOB_TYPE_STREAMING",
              "JOB_STATE_CANCELLED", "STALE", "2.23.0-SNAPSHOT", "europe-west2", 0, "2020-08-06T12:48:11.885373Z" }, 
            { "2020-08-06_05_28_24-6040859398619218325", "bt-dataflow-sql-demo", "beamsqldemopipeline-ihr-0806122805-aa08f704", "JOB_TYPE_STREAMING",
              "JOB_STATE_CANCELLED", "SUPPORTED", "2.23.0", "europe-west1", 0 , "2020-08-06T12:28:26.499518Z"}, 
            { "2020-08-06_03_53_30-15110274477063034935", "bt-dataflow-sql-demo", "dfsql-65f4a130-173c3645af2", "JOB_TYPE_STREAMING",
              "JOB_STATE_CANCELLED", "STALE", "2.23.0-SNAPSHOT", "us-central1", 0, "2020-08-06T10:53:31.543230Z" }, 
            { "2020-08-06_03_50_01-9654167820707455539", "bt-dataflow-sql-demo", "dfsql-43d5b99-173c357c23e", "JOB_TYPE_STREAMING",
              "JOB_STATE_CANCELLED", "STALE", "2.23.0-SNAPSHOT", "us-central1", 0, "2020-08-06T10:50:02.089393Z" },
            { "2020-08-06_00_47_39-7199932943153518565", "bt-dataflow-sql-demo", "beamsqldemopipeline-ihr-0806074705-d05f6ed7", "JOB_TYPE_STREAMING",
              "JOB_STATE_CANCELLED", "SUPPORTED", "2.23.0", "europe-west1", 0, "2020-08-06T07:47:41.890279Z" },
            { "2020-08-05_02_07_44-11496906389150035870", "bt-dataflow-sql-demo", "dfsql-join-and-window", "JOB_TYPE_STREAMING", 
              "JOB_STATE_CANCELLED", "STALE", "2.23.0-SNAPSHOT", "us-central1", 0, "2020-08-05T09:07:45.269405Z" },
            { "2020-08-05_01_27_19-12034076432132404549", "bt-dataflow-sql-demo", "dfsql-query-example-topic-table", "JOB_TYPE_STREAMING", 
              "JOB_STATE_CANCELLED", "STALE", "2.23.0-SNAPSHOT", "us-central1", 0, "2020-08-05T08:27:20.292802Z" },
            { "2020-08-04_14_46_51-10622717568042133093", "bt-dataflow-sql-demo", "beamsqldemopipeline-ihr-0804214611-d6c2f941", "JOB_TYPE_STREAMING",
              "JOB_STATE_CANCELLED", "SUPPORTED", "2.23.0", "europe-west1", 0, "2020-08-04T21:46:53.637572Z" },
            { "2020-08-04_14_33_26-5454611298510143581", "bt-dataflow-sql-demo", "beamsqldemopipeline-ihr-0804213246-8dbbe1e9", "JOB_TYPE_STREAMING", 
              "JOB_STATE_CANCELLED", "SUPPORTED", "2.23.0", "europe-west1", 0, "2020-08-04T21:33:28.350686Z" },
            { "2020-08-04_13_09_00-4586883393904972800", "bt-dataflow-sql-demo", "beamsqldemopipeline-ihr-0804200823-809e448a", "JOB_TYPE_STREAMING", 
              "JOB_STATE_FAILED", "SUPPORTED", "2.23.0", "europe-west1", 0, "2020-08-04T20:09:02.212104Z" } }); // Failed
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

    public JobModelTest(final String jobId, final String projectId, final String name, final String type,
                                        final String status, final String sdkStatus, final String sdk, 
                                            final String region, final int currentWorkers, final String startTime)
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


}