// Copyright 2020 Google LLC

/**
 * @author andreeanica
 */

package com.google.sps.data;

import com.google.api.services.dataflow.model.Job;
import java.io.IOException;
import java.lang.IllegalArgumentException;

// Interface designed to separate fetching from the JobModel implementation
// to make it possible to test
public interface FetchJobInfo {
  String getName();
  String getId();
  String getType();
  String getSdk();
  String getSdkSupportStatus();
  String getRegion();
  int getCurrentWorkers();
  String getSdkName();
  String getStartTime();
  String getState();
  String getStateTime();

  // Modify the job provided as parameter, setting the metrics in it
  void setMetrics(JobModel job, String startTime) throws IOException, IllegalArgumentException;
}