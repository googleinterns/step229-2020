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
}