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
import com.google.api.services.dataflow.model.Job;
import com.google.api.services.dataflow.Dataflow;
import java.io.IOException;
import java.lang.IllegalArgumentException;


// Class that deals with jobs that are stopped, finalised or
// that failed and cant't have their status changed in the
// future
public final class FinalisedJob extends JobModel {
    public FinalisedJob(String projectId, Job job, Dataflow dataflowService) throws IOException, IllegalArgumentException {
        super(projectId, job, dataflowService);

        state = job.getCurrentState();
        stateTime = job.getCurrentStateTime();
    }
}