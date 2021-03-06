// Copyright 2020 Google LLC

/**
 * @author andreeanica
 */

package com.google.sps.data;

import java.sql.Timestamp;
import com.google.api.services.dataflow.model.Job;
import com.google.api.services.dataflow.Dataflow;
import java.io.IOException;
import java.lang.IllegalArgumentException;



// Class that deals with jobs that can have their status modified
// This includes jobs with the following states:
// - JOB_STATE_UNKNOWN
// - JOB_STATE_STOPPED
// - JOB_STATE_RUNNING
// - JOB_STATE_DRAINING
// - JOB_STATE_PENDING
// - JOB_STATE_CANCELLING
// - JOB_STATE_QUEUED
public final class RunningJob extends JobModel {
    public RunningJob(String projectId, FetchJobInfo fetchJobInfo) throws IOException, IllegalArgumentException {
        super(projectId, fetchJobInfo);

        state = fetchJobInfo.getState();
        stateTime = fetchJobInfo.getStateTime();
    }

    public RunningJob(String projectId, String id, String state, String stateTime, String region, String type) throws IOException, IllegalArgumentException {
      super(projectId, id, region, type);

      this.state = state;
      this.stateTime = stateTime;
    }
}