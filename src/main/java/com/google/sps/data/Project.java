// Copyright 2020 Google LLC

/**
 * @author andreeanica
 */

package com.google.sps.data;
import java.time.Instant;
import java.time.Clock;

public final class Project {
    String projectId;
    String lastAccessed;


    public Project(String projectId) {
      this.projectId = projectId;
      this.lastAccessed = java.time.Clock.systemUTC().instant().toString();
    }

}