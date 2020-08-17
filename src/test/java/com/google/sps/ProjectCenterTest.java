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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.io.IOException;
import java.security.GeneralSecurityException;

@RunWith(JUnit4.class)
public final class ProjectCenterTest {
    private ProjectCenter projectCenter;

    @Before
    public void setUp() throws IOException {
      projectCenter = new ProjectCenter("bt-dataflow-sql-demo", "bt-dataflow-sql-demo.json");
    }

    @Test
    public void fetchJobsTest() throws IOException, GeneralSecurityException {
      int actualNumberJobs = projectCenter.fetchJobs().size();
      int expectedNumberJobs = 17;
      Assert.assertEquals(actualNumberJobs, expectedNumberJobs);
  }
}