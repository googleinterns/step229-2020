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


// Class that deals with jobs that are in terminal state.
// This class includes jobs that have the following states:
// - JOB_STATE_DONE
// - JOB_STATE_FAILED
// - JOB_STATE_CANCELLED
// - JOB_STATE_UPDATED
// - JOB_STATE_DRAINED
public final class FinalisedJob extends JobModel {
    public FinalisedJob(String projectId, Job job, Dataflow dataflowService) throws IOException, IllegalArgumentException {
        super(projectId, job, dataflowService);

        state = job.getCurrentState();
        stateTime = job.getCurrentStateTime();
    }
}