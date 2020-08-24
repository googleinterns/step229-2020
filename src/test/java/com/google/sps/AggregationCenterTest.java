// Copyright 2020 Google LLC

/**
 * @author andreeanica
 */

package com.google.sps;

import org.junit.Ignore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.io.IOException;
import com.google.sps.data.JobJSON;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

@RunWith(JUnit4.class)
public final class AggregationCenterTest {
    private AggregationCenter aggregationCenter;

    private static final JobJSON JOB1 = new JobJSON(null, "beamsqldemopipeline-ihr-0804214611-d6c2f941", null,
                                                        null, null, null, null, 0, null, null, null, null, 
                                                            null, null, null, null, null, null, null, null);

    @Before
    public void setUp() {
      aggregationCenter = new AggregationCenter();
    }

    @Test
    public void OneJobOneName()  {
      List<JobJSON> jobs = Arrays.asList(JOB1);

      Map<String, List<JobJSON>> expectedMap = new HashMap<>();
      expectedMap.put("beamsqldemopipeline", jobs);

      Map<String, List<JobJSON>> actualMap = aggregationCenter.aggregateByName(jobs);
     
      Assert.assertEquals(expectedMap, actualMap);
    }
}