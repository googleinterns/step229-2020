// Copyright 2020 Google LLC

/**
 * @author andreeanica
 */

package com.google.sps;

import com.google.sps.data.JobModel;

// Calculates the price for a job   
public final class PriceCenter {
  private double batchCPUhour = 0.056;
  private double batchMemoryGBHour = 0.003557;
  private double batchHDDGBHour= 0.000054;
  private double batchSSDGBHour = 0.000298;
  private double batchProcessedGB = 0.011;

  private double streamingCPUhour = 0.069;
  private double streamingMemoryGBHour = 0.003557;
  private double streamingHDDGBHour= 0.000054;
  private double streamingSSDGBHour = 0.000298;
  private double streamingProcessedGB = 0.018;

  public double calculatePrice(JobModel job) {
      double price = 0;
      if (job.type.compareTo("JOB_TYPE_STREAMING") == 0) {
        if (job.totalVCPUTime != null)
          price += (job.totalVCPUTime / 3600) * streamingCPUhour;
        if (job.totalMemoryTime != null)
          price += (job.totalMemoryTime / (3600 * 1024)) * streamingMemoryGBHour;
        if (job.totalDiskTimeHDD != null)
          price += (job.totalDiskTimeHDD / 3600) * streamingHDDGBHour;
        if (job.totalDiskTimeSSD != null)
          price += (job.totalDiskTimeSSD / 3600) * streamingSSDGBHour;
        if (job.enableStreamingEngine) {
            price += job.totalStreamingData * streamingProcessedGB;
        }
      }

      if (job.type.compareTo("JOB_TYPE_BATCH") == 0) {
        if (job.totalVCPUTime != null)
          price += (job.totalVCPUTime / 3600) * batchCPUhour;
        if (job.totalMemoryTime != null)
          price += (job.totalMemoryTime / (3600 * 1024)) * batchMemoryGBHour;
        if (job.totalDiskTimeHDD != null)
          price += (job.totalDiskTimeHDD / 3600) * batchHDDGBHour;
        if (job.totalDiskTimeSSD != null)
          price += (job.totalDiskTimeSSD / 3600) * batchSSDGBHour;
        if (job.enableStreamingEngine) {
            price += job.totalStreamingData * batchProcessedGB;
        }
      }

      return price;
  }


}