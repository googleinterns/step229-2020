// Copyright 2020 Google LLC

/**
 * @author andreeanica
 */

package com.google.sps;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import com.google.sps.data.JobJSON;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

// Groups the jobs by different critiria
// All methods return Map<String, List<JobJSON>>, which
// represents the jobs groupped
public final class AggregationCenter {
  private Pattern jobPattern;

  public AggregationCenter() {
    jobPattern = Pattern.compile("(?i)([a-z0-9]+)-([a-z0-9]+)-(\\d+)-(.+)");
  }

  // helper function used to group the jobs either by name, or by user, by specifying
  // the idex of the corresponding group. 
  private Map<String, List<JobJSON>> agreggateByGroupIndex(List<JobJSON> jobs, int index) {
    Map<String, List<JobJSON>> groupedJobs = new HashMap<>();

    for (JobJSON job : jobs) {
      if (job.name == null) {
        continue;
      }

      Matcher m = jobPattern.matcher(job.name);

      if (m.find()) {
        // Substracts the group from the pattern based on the given index
        String key = m.group(index);

        if (groupedJobs.containsKey(key)) {
            groupedJobs.get(key).add(job);
        } else {
            List<JobJSON> list = new ArrayList<JobJSON>();
            list.add(job);
            groupedJobs.put(key, list);
        }
      }
    }

    return groupedJobs;
  }

  public Map<String, List<JobJSON>> aggregateByUser(List<JobJSON> jobs) {
      return agreggateByGroupIndex(jobs, 2);
  }

  public Map<String, List<JobJSON>> aggregateByName(List<JobJSON> jobs) {
      return agreggateByGroupIndex(jobs, 1);
  }

  private Map<String, List<JobJSON>> aggregateBy(List<JobJSON> jobs, KeyGenerator keyGenerator) {
    Map<String, List<JobJSON>> groupedJobs = new HashMap<>();

    for (JobJSON job : jobs) {
      String key = keyGenerator.computeKey(job);

      if (key == null) {
        continue;
      }
      
      if (groupedJobs.containsKey(key)) {
        groupedJobs.get(key).add(job);
      } else {
        List<JobJSON> list = new ArrayList<JobJSON>();
        list.add(job);
        groupedJobs.put(key, list);
      }
    }

    return groupedJobs;
  }

  public Map<String, List<JobJSON>> aggregateByRegion(List<JobJSON> jobs) {
    return aggregateBy(jobs, new KeyGenerator() {
      public String computeKey(JobJSON job) {
          return job.region;
      }
    }) ;
  }

  public Map<String, List<JobJSON>> aggregateBySDK(List<JobJSON> jobs) {
    return aggregateBy(jobs, new KeyGenerator() {
      public String computeKey(JobJSON job) {
          if (job.sdk == null && job.sdkSupportStatus == null) {
            return null;
          }

          return job.sdk + " " + job.sdkSupportStatus;
      }
    }) ;
  }

  public Map<String, List<JobJSON>> aggregateBySDKSupportStatus(List<JobJSON> jobs) {
    return aggregateBy(jobs, new KeyGenerator() {
      public String computeKey(JobJSON job) {
          return job.sdkSupportStatus;
      }
    }) ;
  }

  public Map<String, List<JobJSON>> aggregateByProgrammingLanguage(List<JobJSON> jobs) {
    return aggregateBy(jobs, new KeyGenerator() {
      public String computeKey(JobJSON job) {
          
        String key = job.sdkName;

        if (key == null) {
          return null;
        }

        if (key.contains("Java")) {
          key = "Java";
        } else if (key.contains("Python")) {
          key = "Python";
        }
        return key;
      }
    }) ;
  }
}

interface KeyGenerator {
    public String computeKey(JobJSON job);
}