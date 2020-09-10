// Copyright 2020 Google LLC

/**
 * @author andreeanica
 */

package com.google.sps;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.Clock;
import java.util.List;
import java.util.ArrayList;
import com.google.sps.data.JobJSON;
import com.google.sps.data.Project;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.api.services.dataflow.model.Job;

// Class used to interract with Datastore
public class DatastoreInteraction implements DatabaseInteraction {
  DatastoreService datastore;

  public DatastoreInteraction() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  public void put(Entity entity) {
    datastore.put(entity);
  }

  public Iterable<Entity> getJobsFromProject(Key projectKey) {
    // By default, ancestor queries include the specified ancestor itself.
    // The following filter excludes the ancestor from the query results.
    Filter keyFilter =
        new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.GREATER_THAN, projectKey);
      
    // If I want to query for specific type of Jobs (Running, Finalised), 
    // give Query constructor the name of the class
    Query queryJobs = new Query().setAncestor(projectKey).setFilter(keyFilter);

    PreparedQuery resultsJobs = datastore.prepare(queryJobs);
    return resultsJobs.asIterable();
  }

  public Entity get(Key k) throws EntityNotFoundException {
    return datastore.get(k);
  }

  public void delete(Key k) {
    datastore.delete(k);
  }
}