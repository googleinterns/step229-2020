// Copyright 2020 Google LLC

/**
 * @author andreeanica
 */

package com.google.sps;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.EntityNotFoundException;

// Interface designed to separate working with Datastore from the actual
// implementation of the web application. It allows creating mock Objects
// that can simulate working with Datastore
public interface DatabaseInteraction {
  void put(Entity entity);
  Iterable<Entity> getJobsFromProject(Key projectKey);
  Entity get(Key k) throws EntityNotFoundException;
  void delete(Key k);
 }